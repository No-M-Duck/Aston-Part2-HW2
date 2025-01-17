package org.example.repository;

import org.example.BaseIntegrationTest;
import org.example.entity.Director;
import org.example.entity.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class MovieRepositoryImplIntegrationTest extends BaseIntegrationTest {

    private final MovieRepositoryImpl movieRepositoryImpl = new MovieRepositoryImpl();
    private final DirectorRepositoryImpl directorRepositoryImpl = new DirectorRepositoryImpl();

    private UUID directorId;

    @BeforeEach
    void setup() {
        // Создаем директора для тестов
        var director = new Director("Steven", "Spielberg", "USA");
        directorRepositoryImpl.create(director);
        directorId = director.getId();

        assertNotNull(directorId, "Director ID should not be null after creation");
    }

    @Test
    void create() {

        Movie movie = new Movie(null, directorId, "Jaws", LocalDate.of(1975, 6, 20), 124, 1);
        boolean isCreated = movieRepositoryImpl.create(movie);

        assertTrue(isCreated, "Movie should be successfully created");
        assertNotNull(movie.getId(), "Movie ID should be assigned by the database");

    }

    @Test
    void createSQLExc() {

        Movie movie = new Movie(null, directorId, "Jaws", LocalDate.of(1975, 6, 20), 124, 1);
        boolean isCreated = movieRepositoryImpl.create(movie);

        assertTrue(isCreated, "Movie should be successfully created");
        assertNotNull(movie.getId(), "Movie ID should be assigned by the database");

    }

    @Test
    void findById() {

        Movie movie = new Movie(null, directorId, "E.T.", LocalDate.of(1982, 6, 11), 115, 2);
        movieRepositoryImpl.create(movie);

        Optional<Movie> retrievedMovie = movieRepositoryImpl.findById(movie.getId());

        assertTrue(retrievedMovie.isPresent(), "Movie should be found by ID");
        assertEquals(movie.getTitle(), retrievedMovie.get().getTitle(), "Titles should match");
        assertEquals(movie.getDirectorId(), retrievedMovie.get().getDirectorId(), "Director IDs should match");
        assertFalse(movieRepositoryImpl.findById(UUID.randomUUID()).isPresent());
    }

    @Test
    void findAll() {

        Movie movie1 = new Movie(null, directorId, "Jurassic Park", LocalDate.of(1993, 6, 11), 127, 3);
        Movie movie2 = new Movie(null, directorId, "Schindler's List", LocalDate.of(1993, 12, 15), 195, 4);

        movieRepositoryImpl.create(movie1);
        movieRepositoryImpl.create(movie2);

        List<Movie> movies = movieRepositoryImpl.findAll();

        assertNotNull(movies, "Movie list should not be null");
        assertTrue(movies.size() >= 2, "There should be at least 2 movies in the list");
    }

    @Test
    void update() {

        Movie movie = new Movie(null, directorId, "Hook", LocalDate.of(1991, 12, 11), 142, 5);
        movieRepositoryImpl.create(movie);

        movie.setTitle("Hook (Updated)");
        movie.setDuration(145);

        boolean isUpdated = movieRepositoryImpl.update(movie);

        assertTrue(isUpdated, "Movie should be successfully updated");

        Optional<Movie> updatedMovie = movieRepositoryImpl.findById(movie.getId());

        assertTrue(updatedMovie.isPresent(), "Updated movie should be found");
        assertEquals("Hook (Updated)", updatedMovie.get().getTitle(), "Updated title should match");
        assertEquals(145, updatedMovie.get().getDuration(), "Updated duration should match");
        movie.setId(UUID.randomUUID());
        assertFalse(movieRepositoryImpl.update(movie));
    }

    @Test
    void delete() {

        Movie movie = new Movie(null, directorId, "The Terminal", LocalDate.of(2004, 6, 18), 128, 6);
        movieRepositoryImpl.create(movie);

        boolean isDeleted = movieRepositoryImpl.delete(movie.getId());

        assertTrue(isDeleted, "Movie should be successfully deleted");

        Optional<Movie> deletedMovie = movieRepositoryImpl.findById(movie.getId());

        assertFalse(deletedMovie.isPresent(), "Deleted movie should not be found");
    }
}