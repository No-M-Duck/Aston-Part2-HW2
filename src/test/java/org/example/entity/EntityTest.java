package org.example.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    void setDirectorFields() {
        Director director =  new Director("Steven", "Spielberg", "USA");
        director.setName("Quentin");
        director.setLastName("Tarantino");
        director.setCountry("Knoxville");

        assertEquals("Quentin",director.getName());
        assertEquals("Tarantino",director.getLastName());
        assertEquals("Knoxville",director.getCountry());

    }

    @Test
    void setMovieFields() {
        Movie movie = new Movie(UUID.randomUUID(), UUID.randomUUID(), "Jaws", LocalDate.of(1975, 6, 20), 124, 1);
        UUID idDirector = UUID.randomUUID();
        LocalDate now = LocalDate.now();
        movie.setDirectorId(idDirector);
        movie.setHall(1);
        movie.setReleaseDate(now);

        assertEquals(idDirector,movie.getDirectorId());
        assertEquals(1,movie.getHall());
        assertTrue(now.isEqual(movie.getReleaseDate()));

    }

}