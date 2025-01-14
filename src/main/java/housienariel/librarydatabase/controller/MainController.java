package housienariel.librarydatabase.controller;

import java.net.URL;
import java.util.ResourceBundle;

import housienariel.librarydatabase.model.dao.AuthorDAO;
import housienariel.librarydatabase.model.dao.BookDAO;
import housienariel.librarydatabase.model.dao.GenreDAO;
import housienariel.librarydatabase.model.queries.AuthorQuery;
import housienariel.librarydatabase.model.queries.BookQuery;
import housienariel.librarydatabase.model.queries.GenreQuery;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

public class MainController implements Initializable {
    @FXML private BookController bookViewController;
    @FXML private AuthorController authorViewController;
    @FXML private SearchController searchViewController;
    @FXML private GenreController genreViewController;

    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private GenreDAO genreDAO;

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

        initializeTask.setOnSucceeded(e -> Platform.runLater(() -> initializeControllers()));
        initializeTask.setOnFailed(e -> Platform.runLater(() -> showError("Error initializing DAOs: " + initializeTask.getException().getMessage())));

        Thread thread = new Thread(initializeTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void initializeDAOs() throws Exception {
        bookDAO = new BookQuery();
        authorDAO = new AuthorQuery();
        genreDAO = new GenreQuery();
    }

    private void initializeControllers() {
        if (bookViewController != null) {
            bookViewController.injectDAOs(bookDAO, genreDAO, authorDAO);
        }
        if (authorViewController != null) {
            authorViewController.injectDAOs(authorDAO, bookDAO);
        }
        if (searchViewController != null) {
            searchViewController.injectDAOs(bookDAO, genreDAO, authorDAO);
        }
        if (genreViewController != null) {
            genreViewController.injectDAOs(genreDAO);
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Initialization Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
