package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.*;
import housienariel.librarydatabase.model.dao.GenreDAO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GenreController implements Initializable {
    @FXML private TextField genreNameField;
    @FXML private TableView<Genre> genreTableView;
    @FXML private Button addGenreButton;

    private GenreDAO genreDAO;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableView();
    }

    public void injectDAOs(GenreDAO genreDAO) {
        this.genreDAO = genreDAO;
        loadGenres();
    }

    @FXML
    private void handleAddGenre() {
        String genreName = genreNameField.getText().trim();

        if (genreName.isEmpty()) {
            showError("Please enter a genre name");
            return;
        }

        Task<Void> addGenreTask = new Task<>() {
            @Override
            protected Void call() throws BooksDbException {
                Genre newGenre = new Genre(0, genreName);
                genreDAO.addGenre(newGenre);
                return null;
            }
        };

        addGenreTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                clearFields();
                loadGenres();
                showSuccess();
            });
        });

        addGenreTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Error adding genre: " + addGenreTask.getException().getMessage());
            });
        });

        Thread thread = new Thread(addGenreTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void setupTableView() {
        // Genre ID Column
        TableColumn<Genre, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("genreId"));

        // Genre Name Column
        TableColumn<Genre, String> nameColumn = new TableColumn<>("Genre Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("genreName"));

        genreTableView.getColumns().addAll(idColumn, nameColumn);
    }

    private void loadGenres() {
        Task<List<Genre>> loadGenresTask = new Task<>() {
            @Override
            protected List<Genre> call() throws BooksDbException {
                return genreDAO.getAllGenres();
            }
        };

        loadGenresTask.setOnSucceeded(e -> {
            genreTableView.getItems().setAll(loadGenresTask.getValue());
        });

        loadGenresTask.setOnFailed(e -> {
            showError("Error loading genres: " + loadGenresTask.getException().getMessage());
        });

        new Thread(loadGenresTask).start();
    }

    private void clearFields() {
        genreNameField.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText("Genre added successfully");
        alert.showAndWait();
    }

}