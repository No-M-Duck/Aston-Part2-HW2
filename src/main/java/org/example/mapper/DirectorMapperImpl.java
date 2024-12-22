package org.example.mapper;

import org.example.dto.DirectorDTO;
import org.example.entity.Director;

public class DirectorMapperImpl implements DirectorMapper {

    public DirectorDTO toDTO(Director director) {
        return new DirectorDTO(
                director.getId(),
                director.getName(),
                director.getLastName(),
                director.getCountry()
        );
    }

    public Director toEntity(DirectorDTO directorDTO) {
        return new Director(
                directorDTO.getId(),
                directorDTO.getName(),
                directorDTO.getLastName(),
                directorDTO.getCountry()
        );
    }
}
