package housienariel.librarydatabase.model.queries;

import housienariel.librarydatabase.connection.DatabaseConnection;
import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.dao.WriterDAO;
import housienariel.librarydatabase.model.dao.AuthorDAO;
import housienariel.librarydatabase.model.queries.AuthorQuery;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    //remove author from book
    @Override
    public void removeAuthorFromBook(String bookISBN, int authorId) throws BooksDbException {
        String query = "DELETE FROM Writer WHERE book_ISBN = ? AND author_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, bookISBN);
            stmt.setInt(2, authorId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error removing author from book", e);
        }
    }

    // get all books by author
    @Override
    public List<String> getBooksByAuthor(int authorId) throws BooksDbException {
        List<String> bookISBNs = new ArrayList<>();
        String query = "SELECT book_ISBN FROM Writer WHERE author_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, authorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookISBNs.add(rs.getString("book_ISBN"));
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error getting books for author", e);
        }
        return bookISBNs;
    }
}