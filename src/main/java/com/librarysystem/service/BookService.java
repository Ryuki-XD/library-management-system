package com.librarysystem.service;

import com.librarysystem.dao.BookDAO;
import com.librarysystem.dao.BookDAOImpl;
import com.librarysystem.model.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service class handling business logic for books.
 */
public class BookService {

    private final BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAOImpl();
    }

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.findAll();
    }

    public Optional<Book> getBookById(int id) throws SQLException {
        return bookDAO.findById(id);
    }

    public Optional<Book> getBookByIsbn(String isbn) throws SQLException {
        return bookDAO.findByISBN(isbn);
    }

    public List<Book> searchBooks(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookDAO.search(keyword.trim());
    }

    /**
     * Adds a new book to the library database.
     * Enforces business rules like validating unique ISBN.
     */
    public void addBook(Book book) throws SQLException, IllegalArgumentException {
        validateBook(book);
        Optional<Book> existing = bookDAO.findByISBN(book.getIsbn());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A book with ISBN " + book.getIsbn() + " already exists.");
        }
        // Set availability to match quantity when creating a book
        book.setAvailable(book.getQuantity());
        bookDAO.save(book);
    }

    /**
     * Updates an existing book's details.
     */
    public void updateBook(Book book) throws SQLException, IllegalArgumentException {
        validateBook(book);
        Optional<Book> existing = bookDAO.findByISBN(book.getIsbn());
        if (existing.isPresent() && existing.get().getId() != book.getId()) {
            throw new IllegalArgumentException("A book with ISBN " + book.getIsbn() + " already exists for another record.");
        }

        // Adjust availability based on the new quantity.
        // Rule: availability cannot exceed new quantity, and can increase if quantity increases.
        int currentIssued = existing.map(b -> b.getQuantity() - b.getAvailable()).orElse(0);
        int newQuantity = book.getQuantity();
        if (newQuantity < currentIssued) {
            throw new IllegalArgumentException("New quantity (" + newQuantity + ") cannot be less than currently issued books (" + currentIssued + ").");
        }
        book.setAvailable(newQuantity - currentIssued);

        bookDAO.update(book);
    }

    /**
     * Deletes a book by ID.
     */
    public void deleteBook(int id) throws SQLException, IllegalArgumentException {
        Optional<Book> existing = bookDAO.findById(id);
        if (existing.isPresent()) {
            Book book = existing.get();
            if (book.getAvailable() < book.getQuantity()) {
                throw new IllegalArgumentException("Cannot delete book. Some copies are currently issued to students.");
            }
            bookDAO.delete(id);
        }
    }

    private void validateBook(Book book) {
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN is required.");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Author is required.");
        }
        if (book.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
    }
}
