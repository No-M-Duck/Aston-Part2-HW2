package org.example.service;

import org.example.dto.MovieDTO;
import org.example.entity.Movie;
import org.example.mapper.MovieMapper;
import org.example.mapper.MovieMapperImpl;
import org.example.repository.MovieRepository;
import org.example.repository.MovieRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieServiceImplTest {

    private MovieRepository movieRepository;
    private MovieMapper movieMapper;
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepositoryImpl.class);
        movieMapper = mock(MovieMapperImpl.class);
        movieService = new MovieServiceImpl(movieRepository, movieMapper);
    }

    @Test
    void createEntity() {
        UUID directorId = UUID.randomUUID();
        MovieDTO movieDTO = new MovieDTO(null, directorId, "X-men",
                LocalDate.of(2012, 6, 23), 153, 1);
        Movie movie = new Movie(null, directorId, "X-men",
                LocalDate.of(2012, 6, 23), 153, 1);

        when(movieMapper.toEntity(movieDTO)).thenReturn(movie);
        when(movieRepository.create(movie)).thenReturn(true);

        assertTrue(movieService.create(movieDTO));

        verify(movieMapper,times(1)).toEntity(movieDTO);
        verify(movieRepository,times(1)).create(movie);
    }

    @Test
    void findEntityById_ShouldReturnMovie() {
        UUID movieId = UUID.randomUUID();
        UUID directorId = UUID.randomUUID();
        Movie movie = new Movie(movieId, directorId, "X-men",
                LocalDate.of(2012, 6, 23), 153, 1);
        MovieDTO movieDTO = new MovieDTO(movieId, directorId, "X-men",
                LocalDate.of(2012, 6, 23), 153, 1);

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieMapper.toDTO(movie)).thenReturn(movieDTO);

        Optional<MovieDTO> result = movieService.findById(movieId);

        assertTrue(result.isPresent(), "��������� �� ������ ���� ������");
        assertEquals(movieDTO, result.get(), "������������ ������ DTO ������ ��������� � ���������");

        verify(movieRepository, times(1)).findById(movieId);
        verify(movieMapper, times(1)).toDTO(movie);
    }
    @Test
    void findEntityById_ShouldReturnEmpty() {
        UUID movieId = UUID.randomUUID();

        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        Optional<MovieDTO> result = movieService.findById(movieId);

        assertTrue(result.isEmpty());
        assertTrue(result.isEmpty());


        verify(movieRepository,times(1)).findById(movieId);
    }
    @Test
    void findEntityById_ShouldThrowException() {
        UUID movieId = UUID.randomUUID();

        when(movieRepository.findById(movieId)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, ()-> movieService.findById(movieId));

        assertEquals("Database error", exception.getMessage());

        verify(movieRepository,times(1)).findById(movieId);
    }

    @Test
    void findAllEntity_ShouldReturnListMovies() {
        UUID movieId1 = UUID.randomUUID();
        UUID directorId1 = UUID.randomUUID();
        Movie movie1 = new Movie(movieId1, directorId1, "X-men",
                LocalDate.of(2012, 6, 23), 153, 1);
        MovieDTO movieDTO1 = new MovieDTO(movieId1, directorId1, "X-men",
                LocalDate.of(2012, 6, 23), 153, 1);

        UUID movieId2 = UUID.randomUUID();
        UUID directorId2 = UUID.randomUUID();
        Movie movie2 = new Movie(movieId2, directorId2, "Logan",
                LocalDate.of(2012, 6, 23), 153, 1);
        MovieDTO movieDTO2 = new MovieDTO(movieId2, directorId2, "Logan",
                LocalDate.of(2012, 6, 23), 153, 1);

        when(movieRepository.findAll()).thenReturn(List.of(movie1, movie2));
        when(movieMapper.toDTO(movie1)).thenReturn(movieDTO1);
        when(movieMapper.toDTO(movie2)).thenReturn(movieDTO2);

        List<MovieDTO> result = movieService.findAll();

        assertEquals(2, result.size());
        assertEquals(movieDTO1, result.get(0));
        assertEquals(movieDTO2, result.get(1));

        verify(movieRepository, times(1)).findAll();
        verify(movieMapper, times(1)).toDTO(movie1);
        verify(movieMapper, times(1)).toDTO(movie2);

    }

    @Test
    void findAllEntity_ShouldReturnListEmpty() {
        when(movieRepository.findAll()).thenReturn(new ArrayList<>());

        assertTrue(movieService.findAll().isEmpty());

        verify(movieRepository,times(1)).findAll();
    }

    @Test
    void findAllEntity_ShouldThrowException() {
        when(movieRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class,()-> movieService.findAll());

        assertEquals("Database error",exception.getMessage());

        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void updateEntity_ShouldReturnTrue() {
        UUID movieId = UUID.randomUUID();
        UUID directorId = UUID.randomUUID();
        Movie movie = new Movie(movieId, directorId, "Logan",
                LocalDate.of(2012, 6, 23), 153, 1);
        MovieDTO movieDTO = new MovieDTO(movieId, directorId, "Logan",
                LocalDate.of(2012, 6, 23), 153, 1);


        when(movieMapper.toEntity(movieDTO)).thenReturn(movie);
        when(movieRepository.update(movie)).thenReturn(true);


        assertTrue(movieService.update(movieDTO));

        verify(movieRepository,times(1)).update(movie);
        verify(movieMapper, times(1)).toEntity(movieDTO);

    }
    @Test
    void updateEntity_ShouldReturnFalse() {
        UUID movieId = UUID.randomUUID();
        UUID directorId = UUID.randomUUID();
        Movie movie = new Movie(movieId, directorId, "Logan",
                LocalDate.of(2012, 6, 23), 153, 1);
        MovieDTO movieDTO = new MovieDTO(movieId, directorId, "Logan",
                LocalDate.of(2012, 6, 23), 153, 1);


        when(movieMapper.toEntity(movieDTO)).thenReturn(movie);
        when(movieRepository.update(movie)).thenReturn(false);


        assertFalse(movieService.update(movieDTO));

        verify(movieRepository,times(1)).update(movie);
        verify(movieMapper, times(1)).toEntity(movieDTO);
    }
    @Test
    void updateEntity_ShouldThrowException() {
        UUID movieId = UUID.randomUUID();
        UUID directorId = UUID.randomUUID();
        Movie movie = new Movie(movieId, directorId, "Logan",
                LocalDate.of(2012, 6, 23), 153, 1);
        MovieDTO movieDTO = new MovieDTO(movieId,directorId,"Logan",
                LocalDate.of(2012, 6, 23), 153, 1);

        when(movieMapper.toEntity(movieDTO)).thenReturn(movie);
        when(movieRepository.update(movie)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> movieService.update(movieDTO));

        assertEquals("Database error", exception.getMessage());

        verify(movieRepository,times(1)).update(movie);
        verify(movieMapper, times(1)).toEntity(movieDTO);
    }

    @Test
    void deleteEntity_ShouldReturnTrue() {
        UUID movieId = UUID.randomUUID();

        when(movieRepository.delete(movieId)).thenReturn(true);

        boolean result = movieService.delete(movieId);

        assertTrue(result);

        verify(movieRepository,times(1)).delete(movieId);

    }
    @Test
    void deleteEntity_ShouldReturnFalse() {
        UUID movieId = UUID.randomUUID();

        when(movieRepository.delete(movieId)).thenReturn(false);

        boolean result = movieService.delete(movieId);

        assertFalse(result);

        verify(movieRepository,times(1)).delete(movieId);
    }
    @Test
    void deleteEntity_ShouldThrowE() {
        UUID movieId = UUID.randomUUID();

        when(movieRepository.delete(movieId)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class,()-> movieService.delete(movieId));

        verify(movieRepository,times(1)).delete(movieId);
    }
}