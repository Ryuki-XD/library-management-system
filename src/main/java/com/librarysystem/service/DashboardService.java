package com.librarysystem.service;

import com.librarysystem.dao.DashboardDAO;
import com.librarysystem.dao.DashboardDAOImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Service layer class aggregating stats for dashboard and reporting logic.
 */
public class DashboardService {

    private final DashboardDAO dashboardDAO;

    public DashboardService() {
        this.dashboardDAO = new DashboardDAOImpl();
    }

    public DashboardService(DashboardDAO dashboardDAO) {
        this.dashboardDAO = dashboardDAO;
    }

    public int getTotalBooks() throws SQLException {
        return dashboardDAO.getTotalBooks();
    }

    public int getTotalStudents() throws SQLException {
        return dashboardDAO.getTotalStudents();
    }

    public int getActiveIssuesCount() throws SQLException {
        return dashboardDAO.getActiveIssues();
    }

    public int getOverdueCount() throws SQLException {
        return dashboardDAO.getOverdueCount();
    }

    public double getTotalFinesCollected() throws SQLException {
        return dashboardDAO.getTotalFinesCollected();
    }

    public Map<String, Integer> getMonthlyIssueStats() throws SQLException {
        return dashboardDAO.getMonthlyIssueStats();
    }

    public Map<String, Integer> getGenreDistribution() throws SQLException {
        return dashboardDAO.getGenreDistribution();
    }

    public List<Map.Entry<String, Integer>> getPopularBooks(int limit) throws SQLException {
        return dashboardDAO.getPopularBooks(limit);
    }
}
