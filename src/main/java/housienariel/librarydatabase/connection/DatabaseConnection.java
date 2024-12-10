package housienariel.librarydatabase.connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/Library";
    private static final String USER = "app_user";
    private static final String PASSWORD = "strong_password";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        // Always return a new connection
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            System.out.println("Database connected successfully!");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}