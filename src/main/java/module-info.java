module housienariel.librarydatabase {
    requires javafx.controls;
    requires javafx.fxml;


    opens housienariel.librarydatabase to javafx.fxml;
    exports housienariel.librarydatabase;
}