package com.librarysystem.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.StageStyle;

import java.util.Optional;

/**
 * Reusable helper for showing styled JavaFX alert dialogs.
 */
public final class AlertHelper {

    private AlertHelper() {
        // Utility class — prevent instantiation
    }

    /**
     * Shows an informational alert.
     */
    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    /**
     * Shows a warning alert.
     */
    public static void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }

    /**
     * Shows an error alert.
     */
    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    /**
     * Shows a confirmation dialog and returns true if the user clicks OK.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Shows a success message (uses INFORMATION type with custom styling).
     */
    public static void showSuccess(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    // ─── Internal ──────────────────────────────────────────────

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    private static void styleAlert(Alert alert) {
        alert.initStyle(StageStyle.UTILITY);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinWidth(350);
        dialogPane.setMinHeight(150);

        // Apply current theme if available
        String currentTheme = ThemeManager.getInstance().getCurrentThemeStylesheet();
        if (currentTheme != null) {
            dialogPane.getStylesheets().add(currentTheme);
        }
    }
}
