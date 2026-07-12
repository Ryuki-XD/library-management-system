package com.librarysystem.util;

import javafx.scene.Scene;

/**
 * Manages dark/light theme toggling across the application.
 * Uses a singleton pattern to maintain theme state globally.
 */
public final class ThemeManager {

    private static final String LIGHT_THEME = "/com/librarysystem/css/light-theme.css";
    private static final String DARK_THEME = "/com/librarysystem/css/dark-theme.css";

    private static ThemeManager instance;
    private boolean darkMode = true; // Default to dark mode
    private Scene currentScene;

    private ThemeManager() {
        // Singleton
    }

    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Registers the main scene for theme management.
     */
    public void setScene(Scene scene) {
        this.currentScene = scene;
        applyTheme();
    }

    /**
     * Toggles between dark and light mode.
     */
    public void toggleTheme() {
        darkMode = !darkMode;
        applyTheme();
    }

    /**
     * Returns whether dark mode is currently active.
     */
    public boolean isDarkMode() {
        return darkMode;
    }

    /**
     * Returns the stylesheet URL string for the current theme.
     */
    public String getCurrentThemeStylesheet() {
        String path = darkMode ? DARK_THEME : LIGHT_THEME;
        var resource = getClass().getResource(path);
        return (resource != null) ? resource.toExternalForm() : null;
    }

    /**
     * Applies the current theme to the registered scene.
     */
    public void applyTheme() {
        if (currentScene == null) return;

        currentScene.getStylesheets().clear();

        String stylesheet = getCurrentThemeStylesheet();
        if (stylesheet != null) {
            currentScene.getStylesheets().add(stylesheet);
        }
    }
}
