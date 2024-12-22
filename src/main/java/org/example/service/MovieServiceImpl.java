package org.example.service;

import org.example.dto.MovieDTO;
import org.example.entity.Movie;
import org.example.mapper.MovieMapper;
import org.example.mapper.MovieMapperImpl;
import org.example.repository.MovieRepository;
import org.example.repository.MovieRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepositoryImpl;

    private final MovieMapper mapper;

    public MovieServiceImpl(MovieRepository movieRepositoryImpl, MovieMapper mapper) {
        this.movieRepositoryImpl = movieRepositoryImpl;
        this.mapper = mapper;
    }

    @Override
    public boolean createEntity(MovieDTO dto) {
        Movie movie = mapper.toEntity(dto);
        return movieRepositoryImpl.create(movie);
    }

    @Override
    public Optional<MovieDTO> findEntityById(UUID id) {
        return movieRepositoryImpl.findById(id).map(mapper::toDTO);
    }

    @Override
    public List<MovieDTO> findAllEntity() {
        return movieRepositoryImpl.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public boolean updateEntity(MovieDTO dto) {
        Movie movie = mapper.toEntity(dto);
        return movieRepositoryImpl.update(movie);
    }

    @Override
    public boolean deleteEntity(UUID id) {
        return movieRepositoryImpl.delete(id);
    }
}
