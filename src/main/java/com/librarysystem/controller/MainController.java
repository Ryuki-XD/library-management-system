package com.librarysystem.controller;

import com.librarysystem.App;
import com.librarysystem.model.User;
import com.librarysystem.service.AuthService;
import com.librarysystem.util.AlertHelper;
import com.librarysystem.util.ThemeManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML private Button btnDashboard;
    @FXML private Button btnBooks;
    @FXML private Button btnStudents;
    @FXML private Button btnIssuedBooks;
    @FXML private Button btnReports;

    @FXML private Label lblLoggedUser;
    @FXML private Label lblUserRole;
    @FXML private Label lblViewTitle;
    @FXML private ToggleButton toggleTheme;

    @FXML private StackPane contentArea;

    private Button currentActiveButton;

    @FXML
    public void initialize() {
        // Load default logged in user session details
        User user = AuthService.getCurrentUser();
        if (user != null) {
            lblLoggedUser.setText(user.getFullName());
            lblUserRole.setText(user.getRole());
        }

        // Apply visual toggle state based on ThemeManager defaults
        toggleTheme.setSelected(ThemeManager.getInstance().isDarkMode());
        updateThemeToggleButtonText();

        // Load dashboard by default on start
        showDashboard(null);
    }

    @FXML
    void showDashboard(ActionEvent event) {
        loadView("fxml/dashboard", "Dashboard", btnDashboard);
    }

    @FXML
    void showBooks(ActionEvent event) {
        loadView("fxml/books", "Book Catalog Management", btnBooks);
    }

    @FXML
    void showStudents(ActionEvent event) {
        loadView("fxml/students", "Student Directory", btnStudents);
    }

    @FXML
    void showIssuedBooks(ActionEvent event) {
        loadView("fxml/issued-books", "Issue & Return Records", btnIssuedBooks);
    }

    @FXML
    void showReports(ActionEvent event) {
        loadView("fxml/reports", "System Analytics & Reports", btnReports);
    }

    @FXML
    void handleThemeToggle(ActionEvent event) {
        ThemeManager.getInstance().toggleTheme();
        updateThemeToggleButtonText();
    }

    @FXML
    void handleLogout(ActionEvent event) {
        boolean confirm = AlertHelper.showConfirmation("Logout Dialog", "Are you sure you want to log out?");
        if (confirm) {
            new AuthService().logout();
            try {
                App.setRoot("fxml/login");
            } catch (IOException e) {
                AlertHelper.showError("System Error", "Failed to load login screen.");
                e.printStackTrace();
            }
        }
    }

    // ─── Private Helpers ───────────────────────────────────────

    private void loadView(String fxmlPath, String title, Button navButton) {
        lblViewTitle.setText(title);
        setActiveNavigation(navButton);

        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath + ".fxml"));
            Parent view = loader.load();
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            AlertHelper.showError("Interface Error", "Failed to load child view: " + fxmlPath + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setActiveNavigation(Button navButton) {
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("sidebar-item-active");
        }
        if (navButton != null) {
            navButton.getStyleClass().add("sidebar-item-active");
            currentActiveButton = navButton;
        }
    }

    private void updateThemeToggleButtonText() {
        if (toggleTheme.isSelected()) {
            toggleTheme.setText("Dark Mode On");
        } else {
            toggleTheme.setText("Light Mode On");
        }
    }
}
