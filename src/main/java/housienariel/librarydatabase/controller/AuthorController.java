package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.*;
import housienariel.librarydatabase.model.dao.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

import java.net.URL;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AuthorController implements Initializable {
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
        // Only do setup that doesn't require DAOs
        setupTableView();
        setupSelectionListener();
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

        try {
            Author author = new Author(
                    0,  // ID will be set by database
                    nameField.getText().trim(),
                    Date.valueOf(dobPicker.getValue())
            );

            authorDAO.addAuthor(author);
            clearFields();
            loadAuthors();
            showSuccess("Author added successfully");

        } catch (BooksDbException e) {
            showError("Error adding author: " + e.getMessage());
        }
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

        try {
            selectedAuthor.setName(nameField.getText().trim());
            selectedAuthor.setAuthorDob(Date.valueOf(dobPicker.getValue()));

            authorDAO.updateAuthor(selectedAuthor);
            clearFields();
            loadAuthors();
            showSuccess("Author updated successfully");
            updateButton.setDisable(true);

        } catch (BooksDbException e) {
            showError("Error updating author: " + e.getMessage());
        }
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

        try {
            List<Author> authors = authorDAO.searchAuthorsByName(searchTerm);
            authorTableView.getItems().setAll(authors);
        } catch (BooksDbException e) {
            showError("Error searching authors: " + e.getMessage());
        }
    }

    private void setupTableView() {
        TableColumn<Author, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("authorId"));

        TableColumn<Author, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Author, Date> dobCol = new TableColumn<>("Date of Birth");
        dobCol.setCellValueFactory(new PropertyValueFactory<>("authorDob"));

        // Book column will be added after DAOs are injected
        authorTableView.getColumns().addAll(idCol, nameCol, dobCol);
    }

    // Add this new method to set up the books column after DAOs are available
    private void setupBooksColumn() {
        TableColumn<Author, String> booksCol = new TableColumn<>("Books");
        booksCol.setCellValueFactory(data -> {
            try {
                List<String> bookISBNs = writerDAO.getBooksByAuthor(data.getValue().getAuthorId());
                List<String> bookTitles = new ArrayList<>();
                for (String isbn : bookISBNs) {
                    Book book = bookDAO.getBookByISBN(isbn);
                    if (book != null) {
                        bookTitles.add(book.getTitle());
                    }
                }
                return new SimpleStringProperty(String.join(", ", bookTitles));
            } catch (BooksDbException e) {
                return new SimpleStringProperty("Error loading books");
            }
        });

        authorTableView.getColumns().add(booksCol);
    }

    private void setupSelectionListener() {
        authorTableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedAuthor = newSelection;
                        populateFields(newSelection);
                        updateButton.setDisable(false);
                        addButton.setDisable(true);
                    }
                }
        );
    }

    private void populateFields(Author author) {
        nameField.setText(author.getName());
        dobPicker.setValue(author.getAuthorDob().toLocalDate());
    }

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
        try {
            authorTableView.getItems().setAll(authorDAO.getAllAuthors());
        } catch (BooksDbException e) {
            showError("Error loading authors: " + e.getMessage());
        }
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