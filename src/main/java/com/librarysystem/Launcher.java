package com.librarysystem;

/**
 * A workaround launcher class to start the JavaFX application.
 * Prevents errors when launching a modular JavaFX application from a shaded JAR.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
