package org.example.mapper;

import org.example.dto.DirectorDTO;
import org.example.entity.Director;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DirectorMapperImplTest {
    private DirectorMapperImpl directorMapper = new DirectorMapperImpl();

    @Test
    void toDTO() {
        UUID randomUUID = UUID.randomUUID();
        DirectorDTO directorDto = new DirectorDTO(randomUUID, "Steven", "Spielberg", "USA");
        Director director = new Director(randomUUID,"Steven", "Spielberg", "USA");


        assertEquals(directorDto, directorMapper.toDTO(director));
    }

    @Test
    void toEntity() {
        UUID randomUUID = UUID.randomUUID();
        DirectorDTO directorDto = new DirectorDTO(randomUUID, "Steven", "Spielberg", "USA");
        Director director = new Director(randomUUID,"Steven", "Spielberg", "USA");


        assertEquals(director, directorMapper.toEntity(directorDto));
    }
}