package housienariel.librarydatabase.model.queries;

import housienariel.librarydatabase.connection.DatabaseConnection;
import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.dao.AuthorDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorQuery implements AuthorDAO {
    private final Connection connection;

    public AuthorQuery() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public void addAuthor(Author author) throws BooksDbException {
        String query = "INSERT INTO Author (name, author_dob) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, author.getName());
            stmt.setDate(2, new java.sql.Date(author.getAuthorDob().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error adding author", e);
        }
    }

    @Override
    public List<Author> getAllAuthors() throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        String query = "SELECT * FROM Author";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Author author = new Author(rs.getInt("author_id"), rs.getString("name"), rs.getDate("author_dob"));
                authors.add(author);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error retrieving all authors", e);
        }
        return authors;
    }

    @Override
    public Author getAuthorById(int authorId) throws BooksDbException {
        String query = "SELECT * FROM Author WHERE author_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, authorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Author(rs.getInt("author_id"), rs.getString("name"), rs.getDate("author_dob"));
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error retrieving author by ID", e);
        }
        return null;
    }

    @Override
    public void updateAuthor(Author author) throws BooksDbException {
        String query = "UPDATE Author SET name = ?, author_dob = ? WHERE author_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, author.getName());
            stmt.setDate(2, new java.sql.Date(author.getAuthorDob().getTime()));
            stmt.setInt(3, author.getAuthorId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error updating author", e);
        }
    }

    @Override
    public void deleteAuthor(int authorId) throws BooksDbException {
        String query = "DELETE FROM Author WHERE author_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, authorId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error deleting author", e);
        }
    }
}