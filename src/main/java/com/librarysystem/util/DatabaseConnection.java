package com.librarysystem.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton database connection manager.
 * Reads configuration from db.properties and provides a reusable JDBC connection.
 */
public final class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private final String url;
    private final String username;
    private final String password;

    private DatabaseConnection() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("db.properties not found in classpath");
            }
            props.load(input);
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");

            String driver = props.getProperty("db.driver");
            if (driver != null && !driver.isBlank()) {
                Class.forName(driver);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }
    }

    /**
     * Returns the singleton instance.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns an active JDBC connection. Reconnects if the previous connection was closed.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    /**
     * Closes the active connection if open.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Tests if a connection can be established.
     *
     * @return true if the connection is successful
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
