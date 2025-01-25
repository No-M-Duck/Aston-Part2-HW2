package org.example.service;

import org.example.dto.DirectorDTO;
import org.example.entity.Director;

import java.util.List;
import java.util.UUID;

public interface DirectorService extends BaseService<DirectorDTO> {

    List<DirectorDTO> findAllWithFilms();

    DirectorDTO findByIdWithFilm(UUID id);
}
