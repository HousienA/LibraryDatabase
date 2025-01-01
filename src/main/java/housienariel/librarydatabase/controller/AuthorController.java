import com.mongodb.client.MongoDatabase;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.Date;
import java.util.ResourceBundle;
import java.net.URL;

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

        Task<Void> addAuthorTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Author author = new Author(0, nameField.getText().trim(), java.util.Date.from(dobPicker.getValue().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
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
            protected Void call() throws Exception {
                selectedAuthor.setName(nameField.getText().trim());
                selectedAuthor.setAuthorDob(java.util.Date.from(dobPicker.getValue().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
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
    private void handleSearchAuthor() {
        String searchTerm = searchAuthorField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadAuthors();
            return;
        }

        Task<List<Author>> searchAuthorsTask = new Task<>() {
            @Override
            protected List<Author> call() throws Exception {
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

    private void loadAuthors() {
        Task<List<Author>> loadAuthorsTask = new Task<>() {
            @Override
            protected List<Author> call() throws Exception {
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

    private void setupSelectionListener() {
        authorTableView.setOnMouseClicked((MouseEvent event) -> {
            selectedAuthor = authorTableView.getSelectionModel().getSelectedItem();
            if (selectedAuthor != null) {
                nameField.setText(selectedAuthor.getName());
                dobPicker.setValue(selectedAuthor.getAuthorDob().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                updateButton.setDisable(false);
            }
        });
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

    private boolean validateInput() {
        return !nameField.getText().trim().isEmpty() && dobPicker.getValue() != null;
    }
}
