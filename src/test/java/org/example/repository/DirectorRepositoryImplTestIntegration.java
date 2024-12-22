package org.example.repository;

import org.example.BaseTestIntegration;
import org.example.entity.Director;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DirectorRepositoryImplTestIntegration extends BaseTestIntegration {

    private final DirectorRepositoryImpl directorRepositoryImpl = new DirectorRepositoryImpl();

    private UUID directorId;


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