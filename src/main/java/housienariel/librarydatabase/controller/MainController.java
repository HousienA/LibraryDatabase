package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.dao.*;
import housienariel.librarydatabase.model.queries.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private BookController bookViewController;
    @FXML private AuthorController authorViewController;
    @FXML private SearchController searchViewController;
    @FXML private GenreController genreViewController;

    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private GenreDAO genreDAO;
    private WriterDAO writerDAO;

    @SuppressWarnings("unused")
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Task<Void> initializeTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                initializeDAOs();
                return null;
            }
        };

        initializeTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                try {
                    initializeControllers();
                } catch (Exception ex) {
                    showError("Error initializing controllers: " + ex.getMessage());
                }
            });
        });

        initializeTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Error initializing DAOs: " + initializeTask.getException().getMessage());
            });
        });

        Thread thread = new Thread(initializeTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void initializeDAOs() throws Exception {
        bookDAO = new BookQuery();
        authorDAO = new AuthorQuery();
        genreDAO = new GenreQuery();
        writerDAO = new WriterQuery();
    }

    private void initializeControllers() {
        if (bookViewController != null) {
            bookViewController.injectDAOs(bookDAO, genreDAO, writerDAO, authorDAO);
        }
        if (authorViewController != null) {
            authorViewController.injectDAOs(authorDAO, writerDAO, bookDAO);
        }
        if (searchViewController != null) {
            searchViewController.injectDAOs(bookDAO, genreDAO, writerDAO);
        }
        if (genreViewController != null) {
            genreViewController.injectDAOs(genreDAO);
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Initialization Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}