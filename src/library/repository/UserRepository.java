package library.repository;

import library.model.User;
import library.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    public void add(User user){
        String sql = "INSERT INTO users (name, password, isAdmin) VALUES(?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.name);
            statement.setString(2, user.password);
            statement.setBoolean(3, user.isAdmin);

            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()){
                user.id = keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting user: " + e.getMessage());
        }
    }

    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("isAdmin")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching users: " + e.getMessage());
        }
        return users;
    }

    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("isAdmin")
                );
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<User> findByName(String name) {
        String sql = "SELECT * FROM users WHERE name = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("isAdmin")
                );
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by name: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void remove(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting book: " + e.getMessage());
        }
    }

    public void update(User user) {
        String sql = "UPDATE users SET name=?, password=?, isAdmin=?, WHERE id=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user.name);
            preparedStatement.setString(2, user.password);
            preparedStatement.setBoolean(3, user.isAdmin);
            preparedStatement.setInt(4, user.id);


            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }
}
