// GenreController.java
package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.*;
import housienariel.librarydatabase.model.dao.GenreDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
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

    @FXML
    private void handleAddGenre() {
        String genreName = genreNameField.getText().trim();

        if (genreName.isEmpty()) {
            showError("Please enter a genre name");
            return;
        }

        try {
            Genre newGenre = new Genre(0, genreName); // ID will be set by database
            genreDAO.addGenre(newGenre);

            clearFields();
            loadGenres();
            showSuccess("Genre added successfully");

        } catch (BooksDbException e) {
            showError("Error adding genre: " + e.getMessage());
        }
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
        try {
            genreTableView.getItems().setAll(genreDAO.getAllGenres());
        } catch (BooksDbException e) {
            showError("Error loading genres: " + e.getMessage());
        }
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

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void injectDAOs(GenreDAO genreDAO) {
        this.genreDAO = genreDAO;
        loadGenres();
    }
}