package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.*;
import housienariel.librarydatabase.model.dao.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;
import java.util.function.Supplier;

public class SearchController {
    @FXML private TextField searchField;
    @FXML private ComboBox<Integer> ratingFilterBox;
    @FXML private ComboBox<Genre> genreComboBox;
    @FXML private TableView<Book> searchResultsTable;
    @FXML private Label resultCountLabel;

    private BookDAO bookDAO;
    private GenreDAO genreDAO;
    private WriterDAO writerDAO;


    @FXML
    private void initialize() {
        setupTableView();
        setupRatingFilter();
    }

    public void injectDAOs(BookDAO bookDAO, GenreDAO genreDAO, WriterDAO writerDAO) {
        this.bookDAO = bookDAO;
        this.genreDAO = genreDAO;
        this.writerDAO = writerDAO;
        setupGenreFilter();
    }

    private void setupTableView() {
        setupBasicColumn("ISBN", book -> book.getISBN());
        setupBasicColumn("Title", book -> book.getTitle());
        setupBasicColumn("Genre", book -> book.getGenre().getGenreName());
        setupBasicColumn("Rating", book -> book.getRating() != null ?
                String.valueOf(book.getRating().getRatingValue()) : "No rating");

        setupAuthorsColumn();
    }

    private void setupBasicColumn(String name, java.util.function.Function<Book, String> valueExtractor) {
        TableColumn<Book, String> column = new TableColumn<>(name);
        column.setCellValueFactory(data -> new SimpleStringProperty(valueExtractor.apply(data.getValue())));
        searchResultsTable.getColumns().add(column);
    }

    private void setupAuthorsColumn() {
    TableColumn<Book, String> authorsColumn = new TableColumn<>("Authors");
    authorsColumn.setCellValueFactory(cellData -> {
        Book book = cellData.getValue();
        SimpleStringProperty property = new SimpleStringProperty("Loading...");

        if (book != null) {
            Task<String> loadAuthorsTask = new Task<>() {
                @Override
                protected String call() throws BooksDbException {
                    List<Author> authors = writerDAO.getAuthorsForBook(book.getISBN());
                    return authors.stream()
                            .map(Author::getName)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("No authors");
                }
            };

            loadAuthorsTask.setOnSucceeded(e -> Platform.runLater(() -> property.set(loadAuthorsTask.getValue())));
            loadAuthorsTask.setOnFailed(e -> Platform.runLater(() -> property.set("Error loading authors")));

            new Thread(loadAuthorsTask).start();
        }

        return property;
    });

    searchResultsTable.getColumns().add(authorsColumn);
}

    private void setupRatingFilter() {
        ratingFilterBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingFilterBox.setPromptText("Select Rating");
        ratingFilterBox.valueProperty().addListener((obs, old, val) -> {
            if (val != null) handleRatingSearch();
        });
    }

    private void setupGenreFilter() {
        Task<List<Genre>> genreLoadTask = new Task<>() {
            @Override
            protected List<Genre> call() throws BooksDbException {
                return genreDAO.getAllGenres();
            }
        };

        genreLoadTask.setOnSucceeded(e -> Platform.runLater(() -> {
            genreComboBox.getItems().addAll(genreLoadTask.getValue());
            genreComboBox.setPromptText("Select Genre");

            // Set up display
            ListCell<Genre> cell = new ListCell<>() {
                @Override
                protected void updateItem(Genre genre, boolean empty) {
                    super.updateItem(genre, empty);
                    setText(empty || genre == null ? null : genre.getGenreName());
                }
            };

            genreComboBox.setCellFactory(view -> new ListCell<Genre>() {
                @Override
                protected void updateItem(Genre genre, boolean empty) {
                    super.updateItem(genre, empty);
                    setText(empty || genre == null ? null : genre.getGenreName());
                }
            });
            genreComboBox.setButtonCell(new ListCell<Genre>() {
                @Override
                protected void updateItem(Genre genre, boolean empty) {
                    super.updateItem(genre, empty);
                    setText(empty || genre == null ? null : genre.getGenreName());
                }
            });

            genreComboBox.valueProperty().addListener((obs, old, val) -> {
                if (val != null) handleGenreSearch();
            });
        }));

        new Thread(genreLoadTask).start();
    }

    private void performSearch(Supplier<Task<List<Book>>> taskSupplier) {
        Task<List<Book>> task = taskSupplier.get();

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            List<Book> results = task.getValue();
            searchResultsTable.getItems().setAll(results);
            resultCountLabel.setText("Results: " + results.size());
        }));

        task.setOnFailed(e -> Platform.runLater(() ->
                showError("Search error: " + task.getException().getMessage())));

        new Thread(task).start();
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        performSearch(() -> new Task<>() {
            @Override
            protected List<Book> call() throws BooksDbException {
                return bookDAO.searchBooks(searchTerm);
            }
        });
    }

    @FXML
    private void handleRatingSearch() {
        Integer rating = ratingFilterBox.getValue();
        if (rating == null) {
            return;
        }

        Task<List<Book>> ratingSearchTask = new Task<>() {
            @Override
            protected List<Book> call() throws BooksDbException {
                return bookDAO.searchBooksByRating(rating);
            }
        };

        ratingSearchTask.setOnSucceeded(e -> {
            List<Book> results = ratingSearchTask.getValue();
            updateSearchResults(results);
        });

        ratingSearchTask.setOnFailed(e -> {
            showError("Error searching by rating: " + ratingSearchTask.getException().getMessage());
        });

        new Thread(ratingSearchTask).start();
    }

    @FXML
    private void handleGenreSearch() {
        Genre selectedGenre = genreComboBox.getValue();
        if (selectedGenre == null) return;

        performSearch(() -> new Task<>() {
            @Override
            protected List<Book> call() throws BooksDbException {
                return bookDAO.searchBooks(selectedGenre.getGenreName());
            }
        });
    }

    @FXML
    private void handleClearSearch() {
        searchField.clear();
        ratingFilterBox.setValue(null);
        searchResultsTable.getItems().clear();
        genreComboBox.setValue(null);
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
}