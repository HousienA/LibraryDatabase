module housienariel.librarydatabase {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens housienariel.librarydatabase to javafx.fxml;
    exports housienariel.librarydatabase;
    exports housienariel.librarydatabase.connection;
    opens housienariel.librarydatabase.connection to javafx.fxml;
}