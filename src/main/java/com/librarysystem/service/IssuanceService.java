package com.librarysystem.service;

import com.librarysystem.dao.BookDAO;
import com.librarysystem.dao.BookDAOImpl;
import com.librarysystem.dao.IssuedBookDAO;
import com.librarysystem.dao.IssuedBookDAOImpl;
import com.librarysystem.model.Book;
import com.librarysystem.model.IssuedBook;
import com.librarysystem.util.DateUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class handling book issues, returns, fine calculations, and active loans.
 */
public class IssuanceService {

    private final IssuedBookDAO issuedBookDAO;
    private final BookDAO bookDAO;

    public IssuanceService() {
        this.issuedBookDAO = new IssuedBookDAOImpl();
        this.bookDAO = new BookDAOImpl();
    }

    public IssuanceService(IssuedBookDAO issuedBookDAO, BookDAO bookDAO) {
        this.issuedBookDAO = issuedBookDAO;
        this.bookDAO = bookDAO;
    }

    public List<IssuedBook> getAllIssues() throws SQLException {
        return issuedBookDAO.findAll();
    }

    public List<IssuedBook> getActiveIssues() throws SQLException {
        return issuedBookDAO.findActive();
    }

    public List<IssuedBook> getOverdueIssues() throws SQLException {
        // First sync/calculate active ones to see if they're overdue
        syncOverdueStatus();
        return issuedBookDAO.findOverdue();
    }

    public List<IssuedBook> getReturnedIssues() throws SQLException {
        return issuedBookDAO.findReturned();
    }

    public List<IssuedBook> searchIssues(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllIssues();
        }
        return issuedBookDAO.search(keyword.trim());
    }

    /**
     * Issues a book to a student.
     * Enforces rules:
     * - Book must have available stock
     * - Student cannot borrow more than MAX_BOOKS_PER_STUDENT (default 3)
     */
    public void issueBook(int bookId, int studentId, LocalDate issueDate) throws SQLException, IllegalArgumentException {
        // Enforce validations
        Optional<Book> bookOpt = bookDAO.findById(bookId);
        if (bookOpt.isEmpty()) {
            throw new IllegalArgumentException("Book not found.");
        }
        Book book = bookOpt.get();
        if (book.getAvailable() <= 0) {
            throw new IllegalArgumentException("Book is out of stock.");
        }

        int activeCount = issuedBookDAO.countActiveByStudent(studentId);
        if (activeCount >= DateUtils.MAX_BOOKS_PER_STUDENT) {
            throw new IllegalArgumentException("Student has already borrowed the maximum allowed books (" + DateUtils.MAX_BOOKS_PER_STUDENT + ").");
        }

        // Create transaction
        LocalDate dueDate = DateUtils.calculateDueDate(issueDate);
        IssuedBook issue = new IssuedBook(0, bookId, studentId, issueDate, dueDate, null, 0.0, "ISSUED");

        // Execute transaction (Decrement stock + Save issue record)
        bookDAO.decrementAvailable(bookId);
        issuedBookDAO.save(issue);
    }

    /**
     * Returns a book.
     * Calculates and returns any fines generated.
     */
    public double returnBook(int issueId, LocalDate returnDate) throws SQLException, IllegalArgumentException {
        Optional<IssuedBook> issueOpt = issuedBookDAO.findById(issueId);
        if (issueOpt.isEmpty()) {
            throw new IllegalArgumentException("Issue record not found.");
        }
        IssuedBook issue = issueOpt.get();
        if ("RETURNED".equals(issue.getStatus())) {
            throw new IllegalArgumentException("Book is already marked as returned.");
        }

        // Calculate fine
        double fine = DateUtils.calculateFine(issue.getDueDate(), returnDate);

        // Update record
        issue.setReturnDate(returnDate);
        issue.setFineAmount(fine);
        issue.setStatus("RETURNED");

        // Save to DB
        issuedBookDAO.updateReturn(issue);
        bookDAO.incrementAvailable(issue.getBookId());

        return fine;
    }

    /**
     * Scans through issued books, checks if any active loan is past due date, and changes their status to OVERDUE.
     */
    public void syncOverdueStatus() throws SQLException {
        List<IssuedBook> active = issuedBookDAO.findActive();
        LocalDate today = LocalDate.now();
        for (IssuedBook ib : active) {
            if (today.isAfter(ib.getDueDate()) && !"OVERDUE".equals(ib.getStatus())) {
                ib.setStatus("OVERDUE");
                // In a production app, we would update status in the database.
                // Since this updates status dynamically, let's execute an update return or similar.
                // We'll write a simple update query in Implementation or direct update.
                issuedBookDAO.updateReturn(ib);
            }
        }
    }
}
