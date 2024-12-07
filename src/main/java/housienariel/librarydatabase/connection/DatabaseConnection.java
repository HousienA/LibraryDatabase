package housienariel.librarydatabase.connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/Library";
    private static final String USER = "app_user";
    private static final String PASSWORD = "strong_password";

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