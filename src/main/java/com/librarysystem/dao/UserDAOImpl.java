package com.librarysystem.dao;

import com.librarysystem.model.User;
import com.librarysystem.util.DatabaseConnection;

import java.sql.*;
import java.util.Optional;

/**
 * JDBC implementation of {@link UserDAO}.
 */
public class UserDAOImpl implements UserDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, full_name, role, created_at FROM users WHERE username = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getRole());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password_hash = ?, full_name = ?, role = ? WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getRole());
            stmt.setInt(5, user.getId());
            stmt.executeUpdate();
        }
    }

    // ─── Row Mapper ────────────────────────────────────────────

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) user.setCreatedAt(ts.toLocalDateTime());
        return user;
    }
}
