package com.librarysystem.dao;

import com.librarysystem.model.IssuedBook;
import com.librarysystem.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link IssuedBookDAO}.
 * Queries use JOINs to populate transient display fields (book title, student name).
 */
public class IssuedBookDAOImpl implements IssuedBookDAO {

    private static final String SELECT_WITH_JOINS =
            "SELECT ib.*, b.title AS book_title, s.name AS student_name, s.student_id AS student_id_code " +
            "FROM issued_books ib " +
            "JOIN books b ON ib.book_id = b.id " +
            "JOIN students s ON ib.student_id = s.id ";

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public List<IssuedBook> findAll() throws SQLException {
        String sql = SELECT_WITH_JOINS + "ORDER BY ib.issue_date DESC";
        return executeQuery(sql);
    }

    @Override
    public List<IssuedBook> findActive() throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE ib.status = 'ISSUED' ORDER BY ib.due_date ASC";
        return executeQuery(sql);
    }

    @Override
    public List<IssuedBook> findOverdue() throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE ib.status = 'OVERDUE' OR (ib.status = 'ISSUED' AND ib.due_date < CURDATE()) ORDER BY ib.due_date ASC";
        return executeQuery(sql);
    }

    @Override
    public List<IssuedBook> findReturned() throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE ib.status = 'RETURNED' ORDER BY ib.return_date DESC";
        return executeQuery(sql);
    }

    @Override
    public List<IssuedBook> findByStudent(int studentId) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE ib.student_id = ? ORDER BY ib.issue_date DESC";
        List<IssuedBook> results = new ArrayList<>();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    @Override
    public List<IssuedBook> findByBook(int bookId) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE ib.book_id = ? ORDER BY ib.issue_date DESC";
        List<IssuedBook> results = new ArrayList<>();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    @Override
    public Optional<IssuedBook> findById(int id) throws SQLException {
        String sql = SELECT_WITH_JOINS + "WHERE ib.id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<IssuedBook> search(String keyword) throws SQLException {
        String sql = SELECT_WITH_JOINS +
                "WHERE b.title LIKE ? OR s.name LIKE ? OR s.student_id LIKE ? " +
                "ORDER BY ib.issue_date DESC";
        List<IssuedBook> results = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    @Override
    public void save(IssuedBook issuedBook) throws SQLException {
        String sql = "INSERT INTO issued_books (book_id, student_id, issue_date, due_date, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, issuedBook.getBookId());
            stmt.setInt(2, issuedBook.getStudentId());
            stmt.setDate(3, Date.valueOf(issuedBook.getIssueDate()));
            stmt.setDate(4, Date.valueOf(issuedBook.getDueDate()));
            stmt.setString(5, issuedBook.getStatus());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    issuedBook.setId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void updateReturn(IssuedBook issuedBook) throws SQLException {
        String sql = "UPDATE issued_books SET return_date = ?, fine_amount = ?, status = ? WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(issuedBook.getReturnDate()));
            stmt.setDouble(2, issuedBook.getFineAmount());
            stmt.setString(3, issuedBook.getStatus());
            stmt.setInt(4, issuedBook.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public int countActive() throws SQLException {
        String sql = "SELECT COUNT(*) FROM issued_books WHERE status = 'ISSUED'";
        return executeCount(sql);
    }

    @Override
    public int countOverdue() throws SQLException {
        String sql = "SELECT COUNT(*) FROM issued_books WHERE status IN ('OVERDUE') OR (status = 'ISSUED' AND due_date < CURDATE())";
        return executeCount(sql);
    }

    @Override
    public int countActiveByStudent(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM issued_books WHERE student_id = ? AND status IN ('ISSUED', 'OVERDUE')";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public double getTotalFinesCollected() throws SQLException {
        String sql = "SELECT COALESCE(SUM(fine_amount), 0) FROM issued_books WHERE status = 'RETURNED'";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    // ─── Helpers ───────────────────────────────────────────────

    private List<IssuedBook> executeQuery(String sql) throws SQLException {
        List<IssuedBook> results = new ArrayList<>();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    private int executeCount(String sql) throws SQLException {
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private IssuedBook mapRow(ResultSet rs) throws SQLException {
        IssuedBook ib = new IssuedBook();
        ib.setId(rs.getInt("id"));
        ib.setBookId(rs.getInt("book_id"));
        ib.setStudentId(rs.getInt("student_id"));

        Date issueDate = rs.getDate("issue_date");
        if (issueDate != null) ib.setIssueDate(issueDate.toLocalDate());

        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) ib.setDueDate(dueDate.toLocalDate());

        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) ib.setReturnDate(returnDate.toLocalDate());

        ib.setFineAmount(rs.getDouble("fine_amount"));
        ib.setStatus(rs.getString("status"));

        // Joined fields
        ib.setBookTitle(rs.getString("book_title"));
        ib.setStudentName(rs.getString("student_name"));
        ib.setStudentIdCode(rs.getString("student_id_code"));

        return ib;
    }
}
