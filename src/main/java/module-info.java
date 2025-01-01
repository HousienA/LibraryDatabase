module housienariel.librarydatabase {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver;

    opens housienariel.librarydatabase to javafx.fxml;
    opens housienariel.librarydatabase.connection to javafx.fxml;
    opens housienariel.librarydatabase.controller to javafx.fxml;
    opens housienariel.librarydatabase.model to javafx.fxml;
    opens housienariel.librarydatabase.model.dao to javafx.fxml;
    opens housienariel.librarydatabase.model.queries to javafx.fxml;

    exports housienariel.librarydatabase;
    exports housienariel.librarydatabase.connection;
    exports housienariel.librarydatabase.controller;
    exports housienariel.librarydatabase.model;
    exports housienariel.librarydatabase.model.dao;
    exports housienariel.librarydatabase.model.queries;
}
