package housienariel.librarydatabase;

import housienariel.librarydatabase.controller.*;
import housienariel.librarydatabase.model.dao.*;
import housienariel.librarydatabase.model.queries.*;
import housienariel.librarydatabase.connection.MongoDBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class LibraryApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/housienariel/librarydatabase/view/MainView.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            primaryStage.setTitle("Library Management System");

<<<<<<< HEAD
=======

>>>>>>> 09cf76f10e6ce1acbc891d42a98dfced8ca6e6a8
            primaryStage.setOnCloseRequest(event -> {
                MongoDBConnection.closeConnection();
            });

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
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
