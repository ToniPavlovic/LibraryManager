package library.repository;

import library.model.Book;
import library.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository {

    public void add(Book book){
        String sql = "INSERT INTO books (title, author, isbn, isBorrowed, borrowedByUSerId, borrowDate, dueDate) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, book.title);
            statement.setString(2, book.author);
            statement.setString(3, book.isbn);
            statement.setBoolean(4, book.isBorrowed);
            statement.setObject(5, book.borrowedByUserId > 0 ? book.borrowedByUserId : null);
            statement.setObject(6, book.borrowDate);
            statement.setObject(7, book.dueDate);

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()){
                book.id = keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting book: " + e.getMessage());
        }
    }

    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("isbn")
                );
                book.isBorrowed = resultSet.getBoolean("isBorrowed");
                book.borrowedByUserId = resultSet.getInt("borrowedByUserId");
                book.borrowDate = resultSet.getObject("borrowDate" , java.time.LocalDate.class);
                book.dueDate = resultSet.getObject("dueDate" , java.time.LocalDate.class);
                books.add(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching books: " + e.getMessage());
        }
        return books;
    }

    public Optional<Book> findById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Book book = new Book(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("isbn")
                );
                book.isBorrowed = resultSet.getBoolean("isBorrowed");
                book.borrowedByUserId = resultSet.getInt("borrowedByUserId");
                book.borrowDate = resultSet.getObject("borrowDate" , java.time.LocalDate.class);
                book.dueDate = resultSet.getObject("dueDate" , java.time.LocalDate.class);
                return Optional.of(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding book: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void remove(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting book: " + e.getMessage());
        }
    }

    public void update(Book book) {
        String sql = "UPDATE books SET title=?, author=?, isbn=?, isBorrowed=?, borrowedByUserId=?, borrowDate=?, dueDate=? WHERE id=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, book.title);
            preparedStatement.setString(2, book.author);
            preparedStatement.setString(3, book.isbn);
            preparedStatement.setBoolean(4, book.isBorrowed);
            preparedStatement.setObject(5, book.borrowedByUserId > 0 ? book.borrowedByUserId : null);
            preparedStatement.setObject(6, book.borrowDate);
            preparedStatement.setObject(7, book.dueDate);
            preparedStatement.setInt(8, book.id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating book: " + e.getMessage());
        }
    }
}
