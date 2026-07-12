package com.librarysystem.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object interface for dashboard aggregate queries.
 */
public interface DashboardDAO {

    int getTotalBooks() throws SQLException;

    int getTotalStudents() throws SQLException;

    int getActiveIssues() throws SQLException;

    int getOverdueCount() throws SQLException;

    double getTotalFinesCollected() throws SQLException;

    /**
     * Returns monthly issue counts for the last 6 months.
     * Map key: month label (e.g., "Jul 2026"), value: issue count.
     */
    Map<String, Integer> getMonthlyIssueStats() throws SQLException;

    /**
     * Returns genre distribution for pie chart.
     * Map key: genre name, value: count of books.
     */
    Map<String, Integer> getGenreDistribution() throws SQLException;

    /**
     * Returns the top N most issued books.
     * Each entry: [book_title, issue_count].
     */
    List<Map.Entry<String, Integer>> getPopularBooks(int limit) throws SQLException;
}
