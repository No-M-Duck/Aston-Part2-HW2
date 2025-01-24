package org.example.repository;

import org.example.controllers.MyAppContextListener;
import org.example.database.DataSource;
import org.example.entity.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class MovieRepositoryImpl implements MovieRepository {

    private final DataSource dataSource;

    public MovieRepositoryImpl() {
        this.dataSource = new DataSource();
    }

    @Override
    public boolean create(Movie entity) {
        String sql = "INSERT INTO movies (director_id, title, release_date, duration, hall) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, entity.getDirectorId());
            statement.setString(2, entity.getTitle());
            statement.setDate(3, Date.valueOf(entity.getReleaseDate()));
            statement.setInt(4, entity.getDuration());
            statement.setInt(5, entity.getHall());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                entity.setId(resultSet.getObject("id", UUID.class));
                return entity.getId()!=null;
            }
        } catch (SQLException e) {
            logger.error("Error creating movie: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Optional<Movie> findById(UUID id) {
        String sql = "SELECT * FROM movies WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapToEntity(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error finding movie by ID: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Movie> findAll() {
        String sql = "SELECT * FROM movies";
        List<Movie> movies = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                movies.add(mapToEntity(resultSet));
            }
        } catch (SQLException e) {
            logger.error("Error finding all movies: {}", e.getMessage(), e);
        }
        return movies;
    }

    @Override
    public boolean update(Movie entity) {
        String sql = "UPDATE movies SET director_id = ?, title = ?, release_date = ?, duration = ?, hall = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, entity.getDirectorId());
            statement.setString(2, entity.getTitle());
            statement.setDate(3, Date.valueOf(entity.getReleaseDate()));
            statement.setInt(4, entity.getDuration());
            statement.setInt(5, entity.getHall());
            statement.setObject(6, entity.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating movie: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean delete(UUID id) {
        String sql = "DELETE FROM movies WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting movie: {}", e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Movie mapToEntity(ResultSet resultSet) throws SQLException {
        UUID id = resultSet.getObject("id", UUID.class);
        UUID directorId = resultSet.getObject("director_id", UUID.class);
        String title = resultSet.getString("title");
        Date releaseDate = resultSet.getDate("release_date");
        int duration = resultSet.getInt("duration");
        int hall = resultSet.getInt("hall");

        return new Movie(id, directorId, title, releaseDate.toLocalDate(), duration, hall);
    }
}
