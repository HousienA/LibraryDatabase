package housienariel.librarydatabase.connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/Library";
    private static final String USER = "root"; //change this according to your MySQL username and password
    private static final String PASSWORD = "Hou2003Sql";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}