package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.dao.*;
import housienariel.librarydatabase.model.queries.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private BookController bookViewController;      // Changed from bookViewController
    @FXML private AuthorController authorViewController;  // Changed from authorViewController
    @FXML private SearchController searchViewController;  // Changed from searchViewController
    @FXML private GenreController genreViewController;    // Changed from genreViewController

    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private GenreDAO genreDAO;
    private WriterDAO writerDAO;
    private RatingDAO ratingDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            initializeDAOs();
            // Use Platform.runLater to ensure child controllers are initialized
            Platform.runLater(() -> {
                try {
                    initializeControllers();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Initialization Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error initializing controllers: " + e.getMessage());
                    alert.showAndWait();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Initialization Error");
            alert.setHeaderText(null);
            alert.setContentText("Error initializing DAOs: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void initializeDAOs() throws Exception {
        bookDAO = new BookQuery();
        authorDAO = new AuthorQuery();
        genreDAO = new GenreQuery();
        writerDAO = new WriterQuery();
        ratingDAO = new RatingQuery();
    }

    private void initializeControllers() {
        if (bookViewController != null) {
            bookViewController.injectDAOs(bookDAO, genreDAO, ratingDAO, writerDAO);
        }
        if (authorViewController != null) {
            authorViewController.injectDAOs(authorDAO, writerDAO, bookDAO);
        }
        if (searchViewController != null) {
            searchViewController.injectDAOs(bookDAO, genreDAO);
        }
        if (genreViewController != null) {
            genreViewController.injectDAOs(genreDAO);
        }
    }
}