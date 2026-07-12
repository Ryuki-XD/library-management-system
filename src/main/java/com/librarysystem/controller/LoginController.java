package com.librarysystem.controller;

import com.librarysystem.App;
import com.librarysystem.service.AuthService;
import com.librarysystem.util.AlertHelper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Run code on startup if required (e.g. set default values)
        Platform.runLater(() -> txtUsername.requestFocus());
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username == null || username.trim().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Username is required.");
            txtUsername.requestFocus();
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            AlertHelper.showWarning("Validation Error", "Password is required.");
            txtPassword.requestFocus();
            return;
        }

        // Run authenticating database task on background thread so the UI is responsive.
        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return authService.login(username.trim(), password);
            }
        };

        loginTask.setOnSucceeded(e -> {
            boolean success = loginTask.getValue();
            if (success) {
                try {
                    App.setRoot("fxml/main");
                } catch (Exception ex) {
                    AlertHelper.showError("System Error", "Failed to load dashboard screen.\n" + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                AlertHelper.showError("Login Failed", "Invalid username or password.");
                txtPassword.clear();
                txtPassword.requestFocus();
            }
        });

        loginTask.setOnFailed(e -> {
            Throwable ex = loginTask.getException();
            AlertHelper.showError("Database Connection Error",
                    "Could not connect to the database. Please make sure MySQL is running.\n\nDetails: " + ex.getMessage());
            ex.printStackTrace();
        });

        new Thread(loginTask).start();
    }
}
