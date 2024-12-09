package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.*;
import housienariel.librarydatabase.model.dao.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class BookController implements Initializable {
    @FXML private TextField isbnField;
    @FXML private TextField titleField;
    @FXML private ComboBox<Genre> genreComboBox;
    @FXML private ComboBox<Integer> ratingComboBox;
    @FXML private TableView<Book> bookTableView;

    private BookDAO bookDAO;
    private GenreDAO genreDAO;
    private RatingDAO ratingDAO;
    private WriterDAO writerDAO;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Only set up things that don't need DAOs
        setupTableView();
        setupRatingComboBox();
    }

    private void setupRatingComboBox() {
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingComboBox.setPromptText("Select rating");
    }

    private void setupGenreComboBox() {
        try {
            genreComboBox.getItems().addAll(genreDAO.getAllGenres());
            genreComboBox.setPromptText("Select genre");
            // Add this line to tell the ComboBox how to display Genre objects
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
            // Also need this for the selected item
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
    private void handleAddBook() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Create book object
            Book book = new Book(
                    isbnField.getText(),
                    titleField.getText(),
                    genreComboBox.getValue()
            );

            // Add rating if selected
            if (ratingComboBox.getValue() != null) {
                Rating rating = new Rating(0, ratingComboBox.getValue());
                book.setRating(rating);
            }

            // Add book to database
            bookDAO.addBook(book);

            clearFields();
            refreshTableView();
            showSuccess("Book added successfully");

        } catch (BooksDbException e) {
            showError("Error adding book: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateBook() {
        Book selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showError("Please select a book to update");
            return;
        }

        try {
            selectedBook.setTitle(titleField.getText());
            selectedBook.setGenre(genreComboBox.getValue());

            if (ratingComboBox.getValue() != null) {
                Rating rating = new Rating(0, ratingComboBox.getValue());
                selectedBook.setRating(rating);
            }

            bookDAO.updateBook(selectedBook);
            refreshTableView();
            showSuccess("Book updated successfully");

        } catch (BooksDbException e) {
            showError("Error updating book: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        if (isbnField.getText().isEmpty() || titleField.getText().isEmpty()
                || genreComboBox.getValue() == null) {
            showError("Please fill in all required fields");
            return false;
        }
        return true;
    }

    @FXML
    private void handleClear() {
        isbnField.clear();
        titleField.clear();
        genreComboBox.setValue(null);
        ratingComboBox.setValue(null);
        bookTableView.getSelectionModel().clearSelection();

        showSuccess("Fields cleared");
    }

    private void setupTableView() {
        // Setup table columns
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getISBN()));

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        bookTableView.getColumns().addAll(isbnCol, titleCol);
    }

    private void refreshTableView() {
        try {
            bookTableView.getItems().setAll(bookDAO.getAllBooks());
        } catch (BooksDbException e) {
            showError("Error refreshing books: " + e.getMessage());
        }
    }



    private void clearFields() {
        isbnField.clear();
        titleField.clear();
        genreComboBox.setValue(null);
        ratingComboBox.setValue(null);
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

    public void injectDAOs(BookDAO bookDAO, GenreDAO genreDAO, RatingDAO ratingDAO, WriterDAO writerDAO) {
        this.bookDAO = bookDAO;
        this.genreDAO = genreDAO;
        this.ratingDAO = ratingDAO;
        this.writerDAO = writerDAO;

        // Setup genre combo box and load table data
        setupGenreComboBox();
        refreshTableView();
    }
}
