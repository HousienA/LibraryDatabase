package housienariel.librarydatabase.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.Book;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Genre;
import housienariel.librarydatabase.model.Rating;
import housienariel.librarydatabase.model.dao.AuthorDAO;
import housienariel.librarydatabase.model.dao.BookDAO;
import housienariel.librarydatabase.model.dao.GenreDAO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class BookController implements Initializable {
    @FXML private TextField isbnField;
    @FXML private TextField titleField;
    @FXML private ComboBox<Genre> genreComboBox;
    @FXML private ComboBox<Integer> ratingComboBox;
    @FXML private TableView<Book> bookTableView;
    @FXML private ListView<Author> selectedAuthorsListView;

    private BookDAO bookDAO;
    private GenreDAO genreDAO;
    private AuthorDAO authorDAO;
    private final List<Author> selectedAuthors = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableView();
        setupRatingComboBox();
        setupSelectionListener();
    }

    public void injectDAOs(BookDAO bookDAO, GenreDAO genreDAO, AuthorDAO authorDAO) {
        this.bookDAO = bookDAO;
        this.genreDAO = genreDAO;
        this.authorDAO = authorDAO;
        setupGenreComboBox();
        refreshTableView();
    }

    private void setupSelectionListener() {
        bookTableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            }
        );
    }

    private void populateFields(Book book) {
        isbnField.setText(book.getISBN());
        titleField.setText(book.getTitle());
        genreComboBox.getItems().stream()
            .filter(genre -> genre.getGenreId().equals(book.getGenre().getGenreId()))
            .findFirst()
            .ifPresent(genreComboBox::setValue);

        ratingComboBox.setValue(book.getRating() != null ? book.getRating().getRatingValue() : null);

        selectedAuthors.clear();
        selectedAuthorsListView.getItems().clear();

        Task<List<Author>> fetchAuthorsTask = new Task<>() {
            @Override
            protected List<Author> call() throws BooksDbException {
                return bookDAO.getBookAuthors(book.getISBN());
            }
        };

        fetchAuthorsTask.setOnSucceeded(e -> {
            selectedAuthors.addAll(fetchAuthorsTask.getValue());
            updateSelectedAuthorsListView();
        });

        fetchAuthorsTask.setOnFailed(e -> {
            showError("Error loading book's authors: " + fetchAuthorsTask.getException().getMessage());
        });

        new Thread(fetchAuthorsTask).start();
    }

    private void setupRatingComboBox() {
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingComboBox.setPromptText("Select rating");
    }

    private void setupGenreComboBox() {
        try {
            genreComboBox.getItems().addAll(genreDAO.getAllGenres());
            genreComboBox.setPromptText("Select genre");
            genreComboBox.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(Genre genre, boolean empty) {
                    super.updateItem(genre, empty);
                    setText(empty || genre == null ? null : genre.getGenreName());
                }
            });
            genreComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Genre genre, boolean empty) {
                    super.updateItem(genre, empty);
                    setText(empty || genre == null ? null : genre.getGenreName());
                }
            });
        } catch (BooksDbException e) {
            showError("Error loading genres: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddBook() {
        Task<Void> addBookTask = new Task<>() {
            @Override
            protected Void call() throws BooksDbException {
                if (!validateInput()) return null;

                Book book = new Book(
                    isbnField.getText(),
                    titleField.getText(),
                    genreComboBox.getValue()
                );

                if (ratingComboBox.getValue() != null) {
                    book.setRating(new Rating(0, ratingComboBox.getValue()));
                }

                bookDAO.addBook(book);
                return null;
            }
        };

        addBookTask.setOnSucceeded(e -> Platform.runLater(() -> {
            clearFields();
            refreshTableView();
            showSuccess("Book added successfully");
        }));

        addBookTask.setOnFailed(e -> Platform.runLater(() -> showError("Error adding book: " + addBookTask.getException().getMessage())));
        new Thread(addBookTask).start();
    }

    @FXML
    private void handleUpdateBook() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showError("Please select a book to update");
            return;
        }

        Task<Void> updateBookTask = new Task<>() {
            @Override
            protected Void call() throws BooksDbException {
                selectedBook.setTitle(titleField.getText());
                selectedBook.setGenre(genreComboBox.getValue());
                if (ratingComboBox.getValue() != null) {
                    selectedBook.setRating(new Rating(0, ratingComboBox.getValue()));
                }

                bookDAO.updateBook(selectedBook);
                return null;
            }
        };

        updateBookTask.setOnSucceeded(e -> Platform.runLater(() -> {
            refreshTableView();
            showSuccess("Book updated successfully");
        }));

        updateBookTask.setOnFailed(e -> Platform.runLater(() -> showError("Error updating book: " + updateBookTask.getException().getMessage())));
        new Thread(updateBookTask).start();
    }

    private boolean validateInput() {
        if (isbnField.getText().isEmpty() || titleField.getText().isEmpty() || genreComboBox.getValue() == null) {
            showError("Please fill in all required fields");
            return false;
        }
        return true;
    }

    private void setupTableView() {
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getISBN()));
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        bookTableView.getColumns().addAll(isbnCol, titleCol);
    }

    private void refreshTableView() {
        Task<List<Book>> refreshTask = new Task<>() {
            @Override
            protected List<Book> call() throws BooksDbException {
                return bookDAO.getAllBooks();
            }
        };

        refreshTask.setOnSucceeded(e -> Platform.runLater(() -> bookTableView.getItems().setAll(refreshTask.getValue())));
        refreshTask.setOnFailed(e -> Platform.runLater(() -> showError("Error refreshing books: " + refreshTask.getException().getMessage())));

        new Thread(refreshTask).start();
    }

    private void updateSelectedAuthorsListView() {
        selectedAuthorsListView.getItems().clear();
        selectedAuthorsListView.getItems().addAll(selectedAuthors);
    }

    private void clearFields() {
        isbnField.clear();
        titleField.clear();
        genreComboBox.setValue(null);
        ratingComboBox.setValue(null);
        selectedAuthors.clear();
        selectedAuthorsListView.getItems().clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
