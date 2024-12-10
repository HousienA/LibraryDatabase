package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.*;
import housienariel.librarydatabase.model.dao.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BookController implements Initializable {
    @FXML private TextField isbnField;
    @FXML private TextField titleField;
    @FXML private ComboBox<Genre> genreComboBox;
    @FXML private ComboBox<Integer> ratingComboBox;
    @FXML private TableView<Book> bookTableView;
    @FXML private TextField authorSearchField;
    @FXML private ListView<Author> selectedAuthorsListView;

    private BookDAO bookDAO;
    private GenreDAO genreDAO;
    private WriterDAO writerDAO;
    private AuthorDAO authorDAO;
    private List<Author> selectedAuthors = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableView();
        setupRatingComboBox();
        setupSelectedAuthorsListView();
        setupSelectionListener();
    }

    public void injectDAOs(BookDAO bookDAO, GenreDAO genreDAO, WriterDAO writerDAO, AuthorDAO authorDAO) {
        this.bookDAO = bookDAO;
        this.genreDAO = genreDAO;
        this.writerDAO = writerDAO;
        this.authorDAO = authorDAO;
        setupGenreComboBox();
        refreshTableView();
    }
    
    // Track the data of the selected book in the table view
    private void setupSelectionListener() {
        bookTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        populateFields(newSelection);
                    }
                }
        );
    }

    // Fill the fields with the data of the selected book
    private void populateFields(Book book) {
        isbnField.setText(book.getISBN());
        titleField.setText(book.getTitle());
        for (Genre genre : genreComboBox.getItems()) {
            if (genre.getGenreId().equals(book.getGenre().getGenreId())) {
                genreComboBox.setValue(genre);
                break;
            }
        }

        if (book.getRating() != null) {
            ratingComboBox.setValue(book.getRating().getRatingValue());
        } else {
            ratingComboBox.setValue(null);
        }

        // Clear previous authors and add the book's authors
        selectedAuthors.clear();
        selectedAuthorsListView.getItems().clear();

        Task<List<Author>> fetchAuthorsTask = getListTask(book);

        new Thread(fetchAuthorsTask).start();
    }

    private Task<List<Author>> getListTask(Book book) {
        Task<List<Author>> fetchAuthorsTask = new Task<>() {
            @Override
            protected List<Author> call() throws BooksDbException {
                return writerDAO.getAuthorsForBook(book.getISBN());
            }
        };

        fetchAuthorsTask.setOnSucceeded(e -> {
            List<Author> bookAuthors = fetchAuthorsTask.getValue();
            selectedAuthors.addAll(bookAuthors);
            updateSelectedAuthorsListView();
        });

        fetchAuthorsTask.setOnFailed(e -> {
            showError("Error loading book's authors: " + fetchAuthorsTask.getException().getMessage());
        });
        return fetchAuthorsTask;
    }

    private void setupRatingComboBox() {
        ratingComboBox.getItems().addAll(1, 2, 3, 4, 5);
        ratingComboBox.setPromptText("Select rating");
    }

    private void setupGenreComboBox() {
        try {
            genreComboBox.getItems().addAll(genreDAO.getAllGenres());
            genreComboBox.setPromptText("Select genre");
            genreComboBox.setCellFactory(listView -> new ListCell<Genre>() {
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
        } catch (BooksDbException e) {
            showError("Error loading genres: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddBook() {
        Task<Void> addBookTask = new Task<>() {
            @Override
            protected Void call() throws BooksDbException {
                if (!validateInput()) {
                    return null;
                }

                Book book = new Book(
                        isbnField.getText(),
                        titleField.getText(),
                        genreComboBox.getValue()
                );

                if (ratingComboBox.getValue() != null) {
                    Rating rating = new Rating(0, ratingComboBox.getValue());
                    book.setRating(rating);
                }

                bookDAO.addBook(book);

                if (!selectedAuthors.isEmpty()) {
                    for (Author author : selectedAuthors) {
                        writerDAO.addAuthorToBook(book.getISBN(), author);
                    }
                }
                return null;
            }
        };

        addBookTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                clearFields();
                refreshTableView();
                showSuccess("Book added successfully");
            });
        });

        addBookTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Error adding book: " + addBookTask.getException().getMessage());
            });
        });

        Thread thread = new Thread(addBookTask);
        thread.setDaemon(true);
        thread.start();
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
                    Rating rating = new Rating(0, ratingComboBox.getValue());
                    selectedBook.setRating(rating);
                }

                bookDAO.updateBook(selectedBook);
                return null;
            }
        };

        updateBookTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                refreshTableView();
                showSuccess("Book updated successfully");
            });
        });

        updateBookTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Error updating book: " + updateBookTask.getException().getMessage());
            });
        });

        new Thread(updateBookTask).start();
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
        selectedAuthors.clear();
        selectedAuthorsListView.getItems().clear();
        showSuccess("Fields cleared");
    }

    private void setupTableView() {
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getISBN()));
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        bookTableView.getColumns().addAll(isbnCol, titleCol);
    }
    
    // To refresh the table view after adding or updating a book
    private void refreshTableView() {
        Task<List<Book>> refreshTask = new Task<>() {
            @Override
            protected List<Book> call() throws BooksDbException {
                return bookDAO.getAllBooks();
            }
        };
        refreshTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                bookTableView.getItems().setAll(refreshTask.getValue());
            });
        });

        refreshTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Error refreshing books: " + refreshTask.getException().getMessage());
            });
        });

        new Thread(refreshTask).start();
    }

    // Search for authors by name when creating a book
    @FXML
    private void handleAuthorSearch() {
        String searchTerm = authorSearchField.getText().trim();
        Task<List<Author>> searchAuthorTask = new Task<>() {
            @Override
            protected List<Author> call() throws BooksDbException {
                return authorDAO.searchAuthorsByName(searchTerm);
            }
        };

        searchAuthorTask.setOnSucceeded(e -> {
            List<Author> authors = searchAuthorTask.getValue();
            if (authors.isEmpty()) {
                showError("No authors found with that name");
                return;
            }
            // Create a popup to select authors
            Dialog<List<Author>> dialog = new Dialog<>();
            dialog.setTitle("Select Authors");
            dialog.setHeaderText("Select one or more authors from the results");

            ListView<Author> authorListView = new ListView<>();
            authorListView.getItems().addAll(authors);
            authorListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            // set list for the search authors
            setupSelectedAuthorsListView(authorListView);

            dialog.getDialogPane().setContent(authorListView);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    return new ArrayList<>(authorListView.getSelectionModel().getSelectedItems());
                }
                return null;
            });

            dialog.showAndWait().ifPresent(selectedAuthors -> {
                for (Author author : selectedAuthors) {
                    if (!this.selectedAuthors.contains(author)) {
                        this.selectedAuthors.add(author);
                    }
                }
                updateSelectedAuthorsListView();
                authorSearchField.clear();
            });
        });

        searchAuthorTask.setOnFailed(e -> {
            showError("Error searching for authors: " + searchAuthorTask.getException().getMessage());
        });

        new Thread(searchAuthorTask).start();
    }

    private void setupSelectedAuthorsListView() {
        setupSelectedAuthorsListView(selectedAuthorsListView);
    }

    // Get authors names when searching in book creation
    private void setupSelectedAuthorsListView(ListView<Author> listView) {
        listView.setCellFactory(lv -> new ListCell<Author>() {
            @Override
            protected void updateItem(Author author, boolean empty) {
                super.updateItem(author, empty);
                if (empty || author == null) {
                    setText(null);
                } else {
                    setText(author.getName());
                }
            }
        });
    }

    private void updateSelectedAuthorsListView() {
        selectedAuthorsListView.getItems().clear();  // Clear current items
        selectedAuthorsListView.getItems().addAll(selectedAuthors);  // Add all selected authors
    }

    private void clearFields() {
        isbnField.clear();
        titleField.clear();
        genreComboBox.setValue(null);
        ratingComboBox.setValue(null);
        authorSearchField.clear();
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
