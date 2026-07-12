package com.librarysystem.dao;

import com.librarysystem.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

/**
 * JDBC implementation of {@link DashboardDAO}.
 */
public class DashboardDAOImpl implements DashboardDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public int getTotalBooks() throws SQLException {
        return executeCount("SELECT COALESCE(SUM(quantity), 0) FROM books");
    }

    @Override
    public int getTotalStudents() throws SQLException {
        return executeCount("SELECT COUNT(*) FROM students");
    }

    @Override
    public int getActiveIssues() throws SQLException {
        return executeCount("SELECT COUNT(*) FROM issued_books WHERE status IN ('ISSUED', 'OVERDUE')");
    }

    @Override
    public int getOverdueCount() throws SQLException {
        return executeCount("SELECT COUNT(*) FROM issued_books WHERE status = 'OVERDUE' OR (status = 'ISSUED' AND due_date < CURDATE())");
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

    @Override
    public Map<String, Integer> getMonthlyIssueStats() throws SQLException {
        String sql = "SELECT DATE_FORMAT(issue_date, '%b %Y') AS month_label, COUNT(*) AS cnt " +
                     "FROM issued_books " +
                     "WHERE issue_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
                     "GROUP BY YEAR(issue_date), MONTH(issue_date), month_label " +
                     "ORDER BY YEAR(issue_date), MONTH(issue_date)";

        Map<String, Integer> stats = new LinkedHashMap<>();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString("month_label"), rs.getInt("cnt"));
            }
        }
        return stats;
    }

    @Override
    public Map<String, Integer> getGenreDistribution() throws SQLException {
        String sql = "SELECT COALESCE(genre, 'Unknown') AS genre, COUNT(*) AS cnt " +
                     "FROM books GROUP BY genre ORDER BY cnt DESC";

        Map<String, Integer> distribution = new LinkedHashMap<>();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                distribution.put(rs.getString("genre"), rs.getInt("cnt"));
            }
        }
        return distribution;
    }

    @Override
    public List<Map.Entry<String, Integer>> getPopularBooks(int limit) throws SQLException {
        String sql = "SELECT b.title, COUNT(ib.id) AS issue_count " +
                     "FROM issued_books ib JOIN books b ON ib.book_id = b.id " +
                     "GROUP BY b.id, b.title ORDER BY issue_count DESC LIMIT ?";

        List<Map.Entry<String, Integer>> results = new ArrayList<>();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(Map.entry(rs.getString("title"), rs.getInt("issue_count")));
                }
            }
        }
        return results;
    }

    // ─── Helper ────────────────────────────────────────────────

    private int executeCount(String sql) throws SQLException {
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
