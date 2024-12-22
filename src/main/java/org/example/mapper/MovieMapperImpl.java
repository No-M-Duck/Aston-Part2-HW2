package org.example.mapper;

import org.example.dto.MovieDTO;
import org.example.entity.Movie;

public class MovieMapperImpl implements MovieMapper {
    @Override
    public MovieDTO toDTO(Movie movie) {
        return new MovieDTO(
                movie.getId(),
                movie.getDirectorId(),
                movie.getTitle(),
                movie.getReleaseDate(),
                movie.getDuration(),
                movie.getHall()
        );
    }

    @Override
    public Movie toEntity(MovieDTO dto) {
        return new Movie(
                dto.getId(),
                dto.getDirectorId(),
                dto.getTitle(),
                dto.getReleaseDate(),
                dto.getDuration(),
                dto.getHall()
        );
    }
}
