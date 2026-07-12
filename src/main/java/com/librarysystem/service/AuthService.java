package com.librarysystem.service;

import com.librarysystem.dao.UserDAO;
import com.librarysystem.dao.UserDAOImpl;
import com.librarysystem.model.User;
import com.librarysystem.util.PasswordUtils;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Service class handling authentication logic.
 */
public class AuthService {

    private final UserDAO userDAO;
    private static User currentUser;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Validates credentials and logs in the user if correct.
     *
     * @param username the username
     * @param password the plain-text password
     * @return true if login is successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean login(String username, String password) throws SQLException {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Returns the currently logged-in user.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
