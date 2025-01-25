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
    public boolean create(MovieDTO dto) {
        Movie movie = mapper.toEntity(dto);
        return movieRepositoryImpl.create(movie);
    }

    @Override
    public Optional<MovieDTO> findById(UUID id) {
        return movieRepositoryImpl.findById(id).map(mapper::toDTO);
    }

    @Override
    public List<MovieDTO> findAll() {
        return movieRepositoryImpl.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public boolean update(MovieDTO dto) {
        Movie movie = mapper.toEntity(dto);
        return movieRepositoryImpl.update(movie);
    }

    @Override
    public boolean delete(UUID id) {
        return movieRepositoryImpl.delete(id);
    }
}
