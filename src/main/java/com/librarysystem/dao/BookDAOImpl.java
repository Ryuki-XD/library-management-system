package com.librarysystem.dao;

import com.librarysystem.model.Book;
import com.librarysystem.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link BookDAO}.
 */
public class BookDAOImpl implements BookDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public List<Book> findAll() throws SQLException {
        String sql = "SELECT * FROM books ORDER BY title";
        List<Book> books = new ArrayList<>();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                books.add(mapRow(rs));
            }
        }
        return books;
    }

    @Override
    public Optional<Book> findById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";

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
    public List<Book> findByTitle(String title) throws SQLException {
        String sql = "SELECT * FROM books WHERE title LIKE ? ORDER BY title";
        return findByPattern(sql, title);
    }

    @Override
    public List<Book> findByAuthor(String author) throws SQLException {
        String sql = "SELECT * FROM books WHERE author LIKE ? ORDER BY author";
        return findByPattern(sql, author);
    }

    @Override
    public Optional<Book> findByISBN(String isbn) throws SQLException {
        String sql = "SELECT * FROM books WHERE isbn = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Book> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ? OR genre LIKE ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }
        }
        return books;
    }

    @Override
    public void save(Book book) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, author, publisher, year, genre, quantity, available) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getYear());
            stmt.setString(6, book.getGenre());
            stmt.setInt(7, book.getQuantity());
            stmt.setInt(8, book.getAvailable());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    book.setId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Book book) throws SQLException {
        String sql = "UPDATE books SET isbn = ?, title = ?, author = ?, publisher = ?, year = ?, genre = ?, quantity = ?, available = ? WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setInt(5, book.getYear());
            stmt.setString(6, book.getGenre());
            stmt.setInt(7, book.getQuantity());
            stmt.setInt(8, book.getAvailable());
            stmt.setInt(9, book.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public int countTotal() throws SQLException {
        return executeCount("SELECT COUNT(*) FROM books");
    }

    @Override
    public int countAvailable() throws SQLException {
        return executeCount("SELECT SUM(available) FROM books");
    }

    @Override
    public void decrementAvailable(int bookId) throws SQLException {
        String sql = "UPDATE books SET available = available - 1 WHERE id = ? AND available > 0";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Book is not available for issue (id=" + bookId + ")");
            }
        }
    }

    @Override
    public void incrementAvailable(int bookId) throws SQLException {
        String sql = "UPDATE books SET available = LEAST(available + 1, quantity) WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        }
    }

    // ─── Helpers ───────────────────────────────────────────────

    private List<Book> findByPattern(String sql, String value) throws SQLException {
        List<Book> books = new ArrayList<>();
        String pattern = "%" + value + "%";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }
        }
        return books;
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

    private Book mapRow(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        book.setYear(rs.getInt("year"));
        book.setGenre(rs.getString("genre"));
        book.setQuantity(rs.getInt("quantity"));
        book.setAvailable(rs.getInt("available"));
        Timestamp ts = rs.getTimestamp("added_at");
        if (ts != null) book.setAddedAt(ts.toLocalDateTime());
        return book;
    }
}
