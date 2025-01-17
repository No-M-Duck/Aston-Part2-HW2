package org.example.mapper;

import org.example.dto.MovieDTO;
import org.example.entity.Movie;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MovieMapperImplTest {

    private MovieMapperImpl movieMapper = new MovieMapperImpl();

    @Test
    void toDTO() {
        UUID randomUUID = UUID.randomUUID();
        Movie movie = new Movie(randomUUID, randomUUID, "X-men",
                LocalDate.of(2012, 6, 23), 153, 1);

        MovieDTO movieDTO = new MovieDTO(randomUUID, randomUUID, "X-men",
                LocalDate.of(2012, 6, 23), 153, 1);

        assertEquals(movieDTO,movieMapper.toDTO(movie));
    }

    @Test
    void toEntity() {
        UUID directorId = UUID.randomUUID();
        MovieDTO movieDTO = new MovieDTO(null, directorId, "X-men",
                LocalDate.of(2012, 6, 23), 153, 1);

        Movie movie = new Movie(null, directorId, "X-men",
                LocalDate.of(2012, 6, 23), 153, 1);

        assertEquals(movie,movieMapper.toEntity(movieDTO));
    }
}