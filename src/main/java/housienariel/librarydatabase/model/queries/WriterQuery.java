package housienariel.librarydatabase.model.queries;

import housienariel.librarydatabase.connection.DatabaseConnection;
import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.dao.WriterDAO;
import housienariel.librarydatabase.model.dao.AuthorDAO;
import housienariel.librarydatabase.model.queries.AuthorQuery;


import java.sql.*;

public class WriterQuery implements WriterDAO {
    private final Connection connection;
    private final AuthorQuery authorQuery;

    public WriterQuery() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
        this.authorQuery = new AuthorQuery();
    }

    @Override
    public void addAuthorToBook(String bookISBN, Author author) throws BooksDbException {
        try {
            connection.setAutoCommit(false);

            // Ensure the author exists
            String findAuthorIdQuery = "SELECT author_id FROM Author WHERE name = ?";
            try (PreparedStatement findStmt = connection.prepareStatement(findAuthorIdQuery)) {
                findStmt.setString(1, author.getName());
                try (ResultSet rs = findStmt.executeQuery()) {
                    if (rs.next()) {
                        author.setAuthorId(rs.getInt("author_id"));
                    } else{
                        authorQuery.addAuthor(author); //if author does not exist, add author

                    }
                }
            }

            // Insert into the Writer table
            String writerQuery = "INSERT INTO Writer (book_ISBN, author_id) VALUES (?, ?)";
            try (PreparedStatement writerStmt = connection.prepareStatement(writerQuery)) {
                writerStmt.setString(1, bookISBN);
                writerStmt.setInt(2, author.getAuthorId());
                writerStmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new BooksDbException("Error rolling back transaction", rollbackEx);
            }
            throw new BooksDbException("Error adding author to book", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error resetting auto-commit", e);
            }
        }
    }
}