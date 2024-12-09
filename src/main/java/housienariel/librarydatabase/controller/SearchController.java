package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.*;
import housienariel.librarydatabase.model.dao.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;

public class SearchController {
    @FXML private TextField searchField;
    @FXML private ComboBox<Integer> ratingFilterBox;
    @FXML private ComboBox<Genre> genreComboBox;
    @FXML private TableView<Book> searchResultsTable;
    @FXML private Label resultCountLabel;

    private BookDAO bookDAO;
    private GenreDAO genreDAO;


    @FXML
    private void initialize() {
        setupTableView();
        setupRatingFilter();
    }

    private void setupTableView() {
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getISBN()));

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Book, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getGenre().getGenreName()));

        TableColumn<Book, String> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(data -> {
            Rating rating = data.getValue().getRating();
            return new SimpleStringProperty(
                    rating != null ? String.valueOf(rating.getRatingValue()) : "No rating"
            );
        });

        TableColumn<Book, String> authorsCol = new TableColumn<>("Authors");
        authorsCol.setCellValueFactory(data -> {
            List<Author> authors = data.getValue().getAuthors();
            String authorNames = authors.stream()
                    .map(Author::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("No authors");
            return new SimpleStringProperty(authorNames);
        });

        searchResultsTable.getColumns().addAll(
                isbnCol, titleCol, genreCol, ratingCol, authorsCol
        );
    }

    private void setupRatingFilter() {
        ratingFilterBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingFilterBox.setPromptText("Select Rating");

        ratingFilterBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                handleRatingSearch();
            }
        });
    }

    private void setupGenreFilter() {
        try {
            genreComboBox.getItems().addAll(genreDAO.getAllGenres());
            genreComboBox.setPromptText("Select Genre");

            // Add this code to display genre names properly
            genreComboBox.setCellFactory(listView -> new ListCell<Genre>() {
                @Override
                protected void updateItem(Genre genre, boolean empty) {
                    super.updateItem(genre, empty);
                    if (empty || genre == null) {
                        setText(null);
                    } else {
                        setText(genre.getGenreName());
                    }
                }
            });

            genreComboBox.setButtonCell(new ListCell<Genre>() {
                @Override
                protected void updateItem(Genre genre, boolean empty) {
                    super.updateItem(genre, empty);
                    if (empty || genre == null) {
                        setText(null);
                    } else {
                        setText(genre.getGenreName());
                    }
                }
            });
        } catch (BooksDbException e) {
            showError("Error loading genres: " + e.getMessage());
        }
    }


    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            showError("Please enter a search term");
            return;
        }

        try {
            List<Book> results = bookDAO.searchBooks(searchTerm);
            updateSearchResults(results);
        } catch (BooksDbException e) {
            showError("Error performing search: " + e.getMessage());
        }
    }

    @FXML
    private void handleRatingSearch() {
        Integer rating = ratingFilterBox.getValue();
        if (rating == null) {
            return;
        }

        try {
            List<Book> results = bookDAO.searchBooksByRating(rating);
            updateSearchResults(results);
        } catch (BooksDbException e) {
            showError("Error searching by rating: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenreSearch() {
        Genre selectedGenre = genreComboBox.getValue();
        if (selectedGenre == null) {
            return;
        }

        try {
            List<Book> results = bookDAO.searchBooks(selectedGenre.getGenreName());
            updateSearchResults(results);
        } catch (BooksDbException e) {
            showError("Error searching by genre: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        ratingFilterBox.setValue(null);
        searchResultsTable.getItems().clear();
        resultCountLabel.setText("Results: 0");
    }

    private void updateSearchResults(List<Book> results) {
        searchResultsTable.getItems().setAll(results);
        resultCountLabel.setText("Results: " + results.size());
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void injectDAOs(BookDAO bookDAO, GenreDAO genreDAO) {
        this.bookDAO = bookDAO;
        this.genreDAO = genreDAO;
        setupGenreFilter();
    }
}