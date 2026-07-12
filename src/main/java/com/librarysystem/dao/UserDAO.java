package com.librarysystem.dao;

import com.librarysystem.model.User;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Data Access Object interface for User entities.
 */
public interface UserDAO {

    /**
     * Finds a user by username.
     */
    Optional<User> findByUsername(String username) throws SQLException;

    /**
     * Persists a new user.
     */
    void save(User user) throws SQLException;

    /**
     * Updates an existing user.
     */
    void update(User user) throws SQLException;
}
