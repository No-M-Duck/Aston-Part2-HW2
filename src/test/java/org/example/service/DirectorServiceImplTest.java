package org.example.service;

import org.example.dto.DirectorDTO;
import org.example.entity.Director;
import org.example.mapper.DirectorMapper;
import org.example.mapper.DirectorMapperImpl;
import org.example.repository.DirectorRepository;
import org.example.repository.DirectorRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class DirectorServiceImplTest {
    @Mock
    private DirectorRepository directorRepository;
    @Mock
    private DirectorMapper directorMapper;
    @InjectMocks
    private DirectorService directorService;

    @BeforeEach
    void setUp() {
        directorRepository = mock(DirectorRepositoryImpl.class);
        directorMapper = mock(DirectorMapperImpl.class);
        directorService = new DirectorServiceImpl(directorRepository, directorMapper);
    }

    @Test
    void createEntity() {
        UUID directorId = UUID.randomUUID();
        Director director = new Director(directorId, "Christopher", "Nolan", "UK");
        DirectorDTO directorDTO = new DirectorDTO(null, "Christopher", "Nolan", "UK");

        when(directorMapper.toEntity(directorDTO)).thenReturn(director);
        when(directorRepository.create(director)).thenReturn(true);

        boolean result = directorService.create(directorDTO);

        assertTrue(result);

        verify(directorMapper, times(1)).toEntity(directorDTO);
        verify(directorRepository, times(1)).create(director);
    }

    @Test
    void findEntityById_ShouldReturnDirector_WhenDirectorExists() {
        UUID directorId = UUID.randomUUID();
        Director director = new Director(directorId, "Steven", "Spielberg", "USA");
        DirectorDTO directorDTO = new DirectorDTO(directorId, "Steven", "Spielberg", "USA");

        when(directorRepository.findById(directorId)).thenReturn(Optional.of(director));
        when(directorMapper.toDTO(director)).thenReturn(directorDTO);

        Optional<DirectorDTO> result = directorService.findById(directorId);

        assertTrue(result.isPresent());
        assertEquals(directorDTO, result.get());

        verify(directorRepository, times(1)).findById(directorId);
        verify(directorMapper, times(1)).toDTO(director);
    }

    @Test
    void findEntityById_ShouldReturnEmpty_WhenDirectorDoesNotExist() {
        UUID directorId = UUID.randomUUID();

        when(directorRepository.findById(directorId)).thenReturn(Optional.empty());

        Optional<DirectorDTO> result = directorService.findById(directorId);

        assertTrue(result.isEmpty());

        verify(directorRepository, times(1)).findById(directorId);

    }

    @Test
    void findById_ShouldThrowException_WhenRepositoryFails() {
        UUID directorId = UUID.randomUUID();

        when(directorRepository.findById(directorId)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class,()-> directorService.findById(directorId));

        assertEquals("Database error", exception.getMessage());

        verify(directorRepository, times(1)).findById(directorId);
    }

    @Test
    void findAllEntity_ShouldReturnListDirectors() {
        UUID directorId1 = UUID.randomUUID();
        UUID directorId2 = UUID.randomUUID();

        Director director1 = new Director(directorId1, "Steven", "Spielberg", "USA");
        DirectorDTO directorDTO1 = new DirectorDTO(directorId1, "Steven", "Spielberg", "USA");

        Director director2 = new Director(directorId2, "David", "Fincher", "USA");
        DirectorDTO directorDTO2 = new DirectorDTO(directorId2, "David", "Fincher", "USA");

        when(directorRepository.findAll()).thenReturn(List.of(director1,director2));

        List<DirectorDTO> result = List.of(directorDTO1, directorDTO2);

        assertEquals(result.size(), directorService.findAll().size());
        assertEquals(directorDTO1, result.get(0));
        assertEquals(directorDTO2, result.get(1));

        verify(directorRepository, times(1)).findAll();
    }
    @Test
    void findAllEntity_ShouldReturnListEmpty() {

        when(directorRepository.findAll()).thenReturn(new ArrayList<Director>());

        List<DirectorDTO> result = directorService.findAll();

        assertTrue(result.isEmpty());

        verify(directorRepository, times(1)).findAll();

    }

    @Test
    void findAllEntity_ShouldThrowException() {

        when(directorRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class,()-> directorService.findAll());

        assertEquals("Database error", exception.getMessage());

        verify(directorRepository, times(1)).findAll();

    }
    @Test
    void updateEntity_ShouldReturnTrue() {
        UUID directorId = UUID.randomUUID();

        Director director = new Director(directorId,"Steven","Spielberg","USA");
        DirectorDTO directorDTO = new DirectorDTO(directorId,"Steven","Spielberg","USA");

        when(directorRepository.update(director)).thenReturn(true);
        when(directorMapper.toEntity(directorDTO)).thenReturn(director);

        assertTrue(directorService.update(directorDTO));

        verify(directorMapper, times(1)).toEntity(directorDTO);
        verify(directorRepository, times(1)).update(director);
    }

    @Test
    void updateEntity_ShouldReturnFalse() {
        UUID directorId = UUID.randomUUID();

        Director director = new Director(directorId,"Steven","Spielberg","USA");
        DirectorDTO directorDTO = new DirectorDTO(directorId,"Steven","Spielberg","USA");

        when(directorRepository.update(director)).thenReturn(false);
        when(directorMapper.toEntity(directorDTO)).thenReturn(director);

        assertFalse(directorService.update(directorDTO));

        verify(directorMapper, times(1)).toEntity(directorDTO);
        verify(directorRepository, times(1)).update(director);
    }

    @Test
    void updateEntity_ShouldThrowException() {
        UUID directorId = UUID.randomUUID();

        Director director = new Director(directorId, "Steven", "Spielberg", "USA");
        DirectorDTO directorDTO = new DirectorDTO(directorId, "Steven", "Spielberg", "USA");

        when(directorMapper.toEntity(directorDTO)).thenReturn(director);
        when(directorRepository.update(director)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> directorService.update(directorDTO));

        assertEquals("Database error", exception.getMessage());

        verify(directorMapper, times(1)).toEntity(directorDTO);
        verify(directorRepository, times(1)).update(director);

    }

    @Test
    void deleteEntity_ShouldReturnTrue() {
        UUID directorId = UUID.randomUUID();

        when(directorRepository.delete(directorId)).thenReturn(true);

        assertTrue(directorService.delete(directorId));

        verify(directorRepository, times(1)).delete(directorId);
    }

    @Test
    void deleteEntity_ShouldReturnFalse() {
        UUID directorId = UUID.randomUUID();

        when(directorRepository.delete(directorId)).thenReturn(false);

        assertFalse(directorService.delete(directorId));

        verify(directorRepository, times(1)).delete(directorId);
    }

    @Test
    void deleteEntity_ShouldThrowE() {
        UUID directorId = UUID.randomUUID();

        when(directorRepository.delete(directorId)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> directorService.delete(directorId));

        assertEquals("Database error", exception.getMessage());

        verify(directorRepository, times(1)).delete(directorId);
    }
}