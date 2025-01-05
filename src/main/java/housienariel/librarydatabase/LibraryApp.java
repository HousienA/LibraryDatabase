package housienariel.librarydatabase;

import java.io.IOException;

import housienariel.librarydatabase.connection.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class LibraryApp extends Application {
    @SuppressWarnings("unused")
    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/housienariel/librarydatabase/view/MainView.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            primaryStage.setTitle("Library Management System");

            primaryStage.setOnCloseRequest(event -> {
                DatabaseConnection.closeConnection();
            });

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Application Error");
            alert.setContentText("Error loading application: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
