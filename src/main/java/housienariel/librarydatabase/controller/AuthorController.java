package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.*;
import housienariel.librarydatabase.model.dao.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AuthorController implements Initializable{
    @FXML private TextField nameField;
    @FXML private DatePicker dobPicker;
    @FXML private TableView<Author> authorTableView;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button clearButton;
    @FXML private TextField searchAuthorField;

    private AuthorDAO authorDAO;
    private Author selectedAuthor;
    private WriterDAO writerDAO;
    private BookDAO bookDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // setupTableView();
        // setupSelectionListener();
        updateButton.setDisable(true);
    }

    public void injectDAOs(AuthorDAO authorDAO, WriterDAO writerDAO, BookDAO bookDAO) {
        this.authorDAO = authorDAO;
        this.writerDAO = writerDAO;
        this.bookDAO = bookDAO;
        setupBooksColumn();
        loadAuthors();
    }

    @FXML
    private void handleAddAuthor() {
        if (!validateInput()) {
            return;
        }

        Task<Void> addAuthorTask = new Task<>() {
            @Override
            protected Void call() throws BooksDbException {
                Author author = new Author(null, nameField.getText().trim(), java.util.Date.valueOf(dobPicker.getValue()));
                authorDAO.addAuthor(author);
                return null;
            }
        };

        addAuthorTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                clearFields();
                loadAuthors();
                showSuccess("Author added successfully");
            });
        });

        addAuthorTask.setOnFailed(e -> {
            showError("Error adding author: " + addAuthorTask.getException().getMessage());
        });

        new Thread(addAuthorTask).start();
    }

    @FXML
    private void handleUpdateAuthor() {
        if (selectedAuthor == null) {
            showError("Please select an author to update");
            return;
        }

        if (!validateInput()) {
            return;
        }

        Task<Void> updateAuthorTask = new Task<>() {
            @Override
            protected Void call() throws BooksDbException {
                selectedAuthor.setName(nameField.getText().trim());
                selectedAuthor.setAuthorDob(java.util.Date.(dobPicker.getValue()));
                authorDAO.updateAuthor(selectedAuthor);
                return null;
            }
        };

        updateAuthorTask.setOnSucceeded(e -> {
            clearFields();
            loadAuthors();
            showSuccess("Author updated successfully");
            updateButton.setDisable(true);
        });

        updateAuthorTask.setOnFailed(e -> {
            showError("Error updating author: " + updateAuthorTask.getException().getMessage());
        });

        new Thread(updateAuthorTask).start();
    }

    @FXML
    private void handleClear() {
        clearFields();
        selectedAuthor = null;
        updateButton.setDisable(true);
        addButton.setDisable(false);
    }

    @FXML
    private void handleSearchAuthor() {
        String searchTerm = searchAuthorField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAuthors();
            return;
        }

        Task<List<Author>> searchAuthorsTask = new Task<>() {
            @Override
            protected List<Author> call() throws BooksDbException {
                return authorDAO.searchAuthorsByName(searchTerm);
            }
        };

        searchAuthorsTask.setOnSucceeded(e -> {
            authorTableView.getItems().setAll(searchAuthorsTask.getValue());
        });

        searchAuthorsTask.setOnFailed(e -> {
            showError("Error searching authors: " + searchAuthorsTask.getException().getMessage());
        });

        new Thread(searchAuthorsTask).start();
    }

    // private void setupTableView() {
    //     TableColumn<Author, String> idCol = new TableColumn<>("ID");
    //     idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthorId()));

    //     TableColumn<Author, String> nameCol = new TableColumn<>("Name");
    //     nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    //     TableColumn<Author, LocalDate> dobCol = new TableColumn<>("Date of Birth");
    //     dobCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthorDob().toLocalDate().toString()));

    //     authorTableView.getColumns().addAll(idCol, nameCol, dobCol);
    // }

    // private void setupBooksColumn() {
    //     TableColumn<Author, String> booksCol = new TableColumn<>("Books");
    //     booksCol.setCellValueFactory(data -> {
    //         Author author = data.getValue();
    //         try {
    //             List<String> bookISBNs = writerDAO.getBooksByAuthor(author.getAuthorId());
    //             List<String> bookTitles = new ArrayList<>();
    //             for (String isbn : bookISBNs) {
    //                 Book book = bookDAO.getBookByISBN(isbn);
    //                 if (book != null) {
    //                     bookTitles.add(book.getTitle());
    //                 }
    //             }
    //             return new SimpleStringProperty(String.join(", ", bookTitles));
    //         } catch (BooksDbException e) {
    //             return new SimpleStringProperty("Error loading books");
    //         }
    //     });

    //     authorTableView.getColumns().add(booksCol);
    // }

    // private void setupSelectionListener() {
    //     authorTableView.getSelectionModel().selectedItemProperty().addListener(
    //             (obs, oldSelection, newSelection) -> {
    //                 if (newSelection != null) {
    //                     selectedAuthor = newSelection;
    //                     nameField.setText(newSelection.getName());
    //                     dobPicker.setValue(newSelection.getAuthorDob().toLocalDate());
    //                     updateButton.setDisable(false);
    //                     addButton.setDisable(true);
    //                 }
    //             }
    //     );
    // }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Please enter author name");
            return false;
        }

        if (dobPicker.getValue() == null) {
            showError("Please select date of birth");
            return false;
        }

        return true;
    }

    private void loadAuthors() {
        Task<List<Author>> loadAuthorsTask = new Task<>() {
            @Override
            protected List<Author> call() throws BooksDbException {
                return authorDAO.getAllAuthors();
            }
        };

        loadAuthorsTask.setOnSucceeded(e -> {
            authorTableView.getItems().setAll(loadAuthorsTask.getValue());
        });

        loadAuthorsTask.setOnFailed(e -> {
            showError("Error loading authors: " + loadAuthorsTask.getException().getMessage());
        });

        new Thread(loadAuthorsTask).start();
    }

    private void clearFields() {
        nameField.clear();
        dobPicker.setValue(null);
        authorTableView.getSelectionModel().clearSelection();
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
}
