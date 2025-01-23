package org.example.repository;

import com.zaxxer.hikari.HikariConfig;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.example.database.DataSource;
import org.example.database.DbUtils;
import org.example.entity.Director;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DirectorRepositoryImplIntegrationTest {

    private final DirectorRepositoryImpl directorRepositoryImpl = new DirectorRepositoryImpl();

    private UUID directorId;


    private static PostgreSQLContainer<?> container =new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3"));

    @BeforeAll
    public static void beforeAll() {
        container
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(1)));
        container.start();
        System.out.println(container.getFirstMappedPort());
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(container.getJdbcUrl());
        config.setUsername(container.getUsername());
        config.setPassword(container.getPassword());
        DataSource.init(config);
        DbUtils.startTest(false);
    }

    @BeforeEach
    public void setUp(){

    }

    @AfterAll
    public static void tearDown() {
        container.stop();
    }
    @Test
    @Order(1)
    void create() {
        Director director = new Director("Steven", "Spielberg", "USA");

        boolean isCreated = directorRepositoryImpl.create(director);
        assertTrue(isCreated);

        directorId = director.getId();

        Optional<Director> retrievedDirector = directorRepositoryImpl.findById(director.getId());
        assertTrue(retrievedDirector.isPresent());
        assertEquals(director.getName(), retrievedDirector.get().getName());
        assertEquals(director.getLastName(), retrievedDirector.get().getLastName());
        assertEquals(director.getCountry(), retrievedDirector.get().getCountry());
        System.out.println("create complete");
    }

    @Test
    @Order(2)
    void findById() {
        Optional<Director> director = directorRepositoryImpl.findById(directorId);

        assertTrue(director.isPresent(), "Director should be found by ID");
        assertEquals("Steven", director.get().getName());
        assertEquals("Spielberg", director.get().getLastName());
        assertEquals("USA", director.get().getCountry());
    }

    @Test
    @Order(3)
    void findAll() {
        List<Director> directors = directorRepositoryImpl.findAll();

        assertNotNull(directors, "Directors list should not be null");
        assertFalse(directors.isEmpty(), "Directors list should not be empty");
        assertTrue(directors.stream().anyMatch(d -> d.getId().equals(directorId)), "List should contain the created director");
    }

    @Test
    @Order(4)
    void update() {
        Director updatedDirector = new Director(directorId, "Steven", "Spielberg", "Germany");
        boolean isUpdated = directorRepositoryImpl.update(updatedDirector);

        assertTrue(isUpdated, "Director should be successfully updated");

        Optional<Director> director = directorRepositoryImpl.findById(directorId);
        assertTrue(director.isPresent());
        assertEquals("Germany", director.get().getCountry(), "Director's country should be updated");
    }

    @Test
    @Order(5)
    void delete() {
        boolean isDeleted = directorRepositoryImpl.delete(directorId);

        assertTrue(isDeleted, "Director should be successfully deleted");

        Optional<Director> director = directorRepositoryImpl.findById(directorId);
        assertFalse(director.isPresent(), "Director should no longer be present in the database");
    }

}