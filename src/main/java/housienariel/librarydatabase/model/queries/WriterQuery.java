package housienariel.librarydatabase.model.queries;

import housienariel.librarydatabase.connection.DatabaseConnection;
import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.dao.WriterDAO;

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

            String findAuthorIdQuery = "SELECT author_id FROM Author WHERE name = ?";
            try (PreparedStatement findStmt = connection.prepareStatement(findAuthorIdQuery)) {
                findStmt.setString(1, author.getName());
                try (ResultSet rs = findStmt.executeQuery()) {
                    if (rs.next()) {
                        author.setAuthorId(rs.getInt("author_id"));
                    }
                }
            }

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

    @Override
    public List<Author> getAuthorsForBook(String isbn) throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        String query = """
        SELECT a.* 
        FROM Author a 
        JOIN Writer w ON a.author_id = w.author_id 
        WHERE w.book_ISBN = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Author author = new Author(
                            rs.getInt("author_id"),
                            rs.getString("name"),
                            rs.getDate("author_dob")
                    );
                    authors.add(author);
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error getting authors for book", e);
        }
        return authors;
    }

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