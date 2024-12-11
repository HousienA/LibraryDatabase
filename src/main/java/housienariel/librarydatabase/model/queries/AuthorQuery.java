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

    /**
     * @param author the author to add
     * @throws BooksDbException if an error occurs while adding the author
     */
    @Override
    public void addAuthor(Author author) throws BooksDbException {
        String query = "INSERT INTO Author (name, author_dob) VALUES (?, ?)";
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, author.getName());
                stmt.setDate(2, new java.sql.Date(author.getAuthorDob().getTime()));
                stmt.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new BooksDbException("Error adding author", e);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Transaction error", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error restoring auto-commit mode", e);
            }
        }
    }


    /**
     * @return a list of all authors
     * @throws BooksDbException if an error occurs while retrieving authors
     */
    @Override
    public List<Author> getAllAuthors() throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        String query = "SELECT * FROM Author";
        try {
            connection.setAutoCommit(false);

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Author author = new Author(rs.getInt("author_id"), rs.getString("name"), rs.getDate("author_dob"));
                    authors.add(author);
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new BooksDbException("Error retrieving all authors", e);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Transaction error", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error restoring auto-commit mode", e);
            }
        }
        return authors;
    }


    /**
     * @param authorId the ID of the author to retrieve
     * @return the author with the specified ID, or null if no author is found
     * @throws BooksDbException if an error occurs while retrieving the author
     */
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


    /**
     * @param author the author to update
     * @throws BooksDbException if an error occurs while updating the author
     */
    @Override
    public void updateAuthor(Author author) throws BooksDbException {
        String query = "UPDATE Author SET name = ?, author_dob = ? WHERE author_id = ?";
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, author.getName());
                stmt.setDate(2, new java.sql.Date(author.getAuthorDob().getTime()));
                stmt.setInt(3, author.getAuthorId());
                stmt.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new BooksDbException("Error updating author", e);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Transaction error", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error restoring auto-commit mode", e);
            }
        }
    }


    /**
     * @param namePattern the pattern to search for in author names
     * @return a list of authors whose names contain the specified pattern
     * @throws BooksDbException if an error occurs while searching for authors
     */
    @Override
    public List<Author> searchAuthorsByName(String namePattern) throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        String query = """
                SELECT DISTINCT a.*
                FROM Author a
                WHERE a.name LIKE ?
                """;

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, "%" + namePattern + "%");
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
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new BooksDbException("Error searching authors by name", e);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Transaction error", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error restoring auto-commit mode", e);
            }
        }
        return authors;
    }
}