package org.example.repository;

import org.example.controllers.MyAppContextListener;
import org.example.database.DataSource;
import org.example.entity.Director;
import org.example.entity.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DirectorRepositoryImpl implements DirectorRepository {

    private final DataSource dataSource;

    public DirectorRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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
    public List<Director> findAllWithFilms() {
        List<Director> directors = new ArrayList<>();
        Map<UUID, Director> mapDirectors = new HashMap<>();
        String sql = """
        SELECT 
            d.id AS director_id,
            d.name AS director_name,
            d.last_name AS director_last_name,
            d.country AS director_country,
            m.id AS movie_id,
            m.title AS movie_title,
            m.release_date AS movie_release_date,
            m.duration AS movie_duration,
            m.hall AS movie_hall
        FROM 
            directors d
        LEFT JOIN 
            movies m ON d.id = m.director_id
        ORDER BY 
            d.id, m.release_date
    """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                UUID directorId = resultSet.getObject("director_id", UUID.class);
                Director director = mapDirectors.get(directorId);
                if (director == null) {
                    director = new Director(directorId,
                            resultSet.getString("director_name"),
                            resultSet.getString("director_last_name"),
                            resultSet.getString("director_country"));
                    mapDirectors.put(directorId, director);
                    directors.add(director);
                }
                UUID movieId = resultSet.getObject("movie_id", UUID.class);
                if (movieId != null) {
                    Movie movie = new Movie(
                            movieId,
                            directorId,
                            resultSet.getString("movie_title"),
                            resultSet.getDate("movie_release_date").toLocalDate(),
                            resultSet.getInt("movie_duration"),
                            resultSet.getInt("movie_hall")
                    );
                    director.getMovies().add(movie);
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding directors with movies", e);
        }
        return directors;
    }

    @Override
    public Director findByIdWithFilm(UUID id) {
        Director director = null;
        String sql = """
                    SELECT 
                        d.id AS director_id,
                        d.name AS director_name,
                        d.last_name AS director_last_name,
                        d.country AS director_country,
                        m.id AS movie_id,
                        m.title AS movie_title,
                        m.release_date AS movie_release_date,
                        m.duration AS movie_duration,
                        m.hall AS movie_hall
                    FROM 
                        directors d
                    LEFT JOIN 
                        movies m ON d.id = m.director_id
                    WHERE 
                        d.id = ?
                    ORDER BY 
                        m.release_date
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (director == null) {
                        director = new Director(
                                rs.getObject("director_id", UUID.class),
                                rs.getString("director_name"),
                                rs.getString("director_last_name"),
                                rs.getString("director_country")
                        );
                    }

                    UUID movieId = rs.getObject("movie_id", UUID.class);
                    if (movieId != null) {
                        Movie movie = new Movie(
                                movieId,
                                id,
                                rs.getString("movie_title"),
                                rs.getDate("movie_release_date").toLocalDate(),
                                rs.getInt("movie_duration"),
                                rs.getInt("movie_hall")
                        );
                        director.getMovies().add(movie);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding director with movies", e);
        }

        return director;
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
