package org.example.repository;

import com.zaxxer.hikari.HikariConfig;
import org.example.database.DataSource;
import org.example.database.DbUtils;
import org.example.entity.Director;
import org.example.entity.Movie;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieRepositoryImplIntegrationTest {



    private  MovieRepositoryImpl movieRepositoryImpl;

    private DirectorRepositoryImpl directorRepositoryImpl;

    private  DataSource dataSource;


    private UUID directorId;

    private static PostgreSQLContainer<?> container =new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3")).withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("test.sql")
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(1)));

    @BeforeAll
    public  void beforeAll() {
        container.start();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(container.getJdbcUrl());
        config.setUsername(container.getUsername());
        config.setPassword(container.getPassword());

        dataSource = new DataSource(config);

        movieRepositoryImpl = new MovieRepositoryImpl(dataSource);
        directorRepositoryImpl = new DirectorRepositoryImpl(dataSource);

        Director director = new Director("Quentin ", "Tarantino", "USA");
        directorRepositoryImpl.create(director);
        directorId = director.getId();


    }

    @AfterAll
    public static void tearDown() {
        container.stop();
    }

    @Test
    void create() {

        Movie movie = new Movie(null, directorId, "Pulp Fiction", LocalDate.of(1994, 6, 20), 154, 1);
        boolean isCreated = movieRepositoryImpl.create(movie);

        assertTrue(isCreated, "Movie should be successfully created");
        assertNotNull(movie.getId(), "Movie ID should be assigned by the database");
        System.out.println("create complete");

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