package com.librarysystem;

import com.librarysystem.util.DatabaseConnection;
import com.librarysystem.util.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX main application bootstrap class.
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Try testing database connection beforehand to verify server status
        if (!DatabaseConnection.getInstance().testConnection()) {
            System.err.println("CRITICAL: Database connection could not be established. Ensure MySQL is running on localhost and properties match.");
        }

        Parent root = loadFXML("fxml/login");
        scene = new Scene(root, 950, 600);

        // Link main scene to global theme manager
        ThemeManager.getInstance().setScene(scene);

        stage.setTitle("Athena Library Management System");
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        // Safe database resources release
        DatabaseConnection.getInstance().closeConnection();
    }

    /**
     * Sets a new root FXML view for the main window.
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        // Auto-reapply theme constraints on window swap
        ThemeManager.getInstance().applyTheme();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
