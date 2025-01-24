package org.example.repository;

import org.example.controllers.MyAppContextListener;
import org.example.database.DataSource;
import org.example.entity.Director;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DirectorRepositoryImpl implements DirectorRepository {

    private final DataSource dataSource;

    public DirectorRepositoryImpl() {
        this.dataSource = new DataSource();
    }

    public boolean create(Director director) {
        String sql = "INSERT INTO directors ( name, last_name, country) VALUES (?, ?, ?) RETURNING id";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, director.getName());
            statement.setString(2, director.getLastName());
            statement.setString(3, director.getCountry());

            try (ResultSet generatedKeys = statement.executeQuery()) {
                if (generatedKeys.next()) {
                    UUID generatedId = (UUID) generatedKeys.getObject(1);
                    director.setId(generatedId);
                }
            }
            return director.getId() != null;
        } catch (SQLException sqlException) {
            logger.error("Error creating director: {}", sqlException.getMessage(), sqlException);
            return false;
        }

    }

    @Override
    public Optional<Director> findById(UUID id) {
        String sql = "SELECT * FROM directors where id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapToEntity(resultSet));
            }

        } catch (SQLException sqlException) {
            logger.error("Error found director: {}", sqlException.getMessage(), sqlException);
        }
        return Optional.empty();
    }

    @Override
    public List<Director> findAll() {
        String sql = "SELECT * FROM directors";
        List<Director> directors = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                directors.add(mapToEntity(resultSet));
            }

        } catch (SQLException sqlException) {
            logger.error("Error getting all values of the Director table:{}", sqlException.getMessage(), sqlException);
        }
        return directors;
    }

    @Override
    public boolean update(Director entity) {
        String sql = "UPDATE directors SET name = ?, last_name = ?, country = ? WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getCountry());
            statement.setObject(4, entity.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            logger.error("Error updating a record in the Director table:{}", sqlException.getMessage(), sqlException);
            return false;
        }
    }

    @Override
    public boolean delete(UUID id) {
        String sql = "DELETE FROM directors WHERE id =?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException sqlException) {
            logger.error("Error deleting a record in the Director table:{}", sqlException.getMessage(), sqlException);
            return false;
        }
    }

    @Override
    public Director mapToEntity(ResultSet resultSet) throws SQLException {
        UUID id = resultSet.getObject("id", UUID.class);
        String name = resultSet.getString("name");
        String lastName = resultSet.getString("last_name");
        String country = resultSet.getString("country");
        return new Director(id, name, lastName, country);
    }
}
