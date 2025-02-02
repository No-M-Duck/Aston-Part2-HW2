package org.example.service;

import org.example.dto.DirectorDTO;
import org.example.entity.Director;
import org.example.mapper.DirectorMapper;
import org.example.repository.DirectorRepository;

import java.util.*;


public class DirectorServiceImpl implements DirectorService {

    private final DirectorRepository directorRepository;

    private final DirectorMapper directorMapper;

    public DirectorServiceImpl(DirectorRepository directorRepository,
                               DirectorMapper directorMapper) {
        this.directorRepository = directorRepository;
        this.directorMapper = directorMapper;
    }

    public boolean create(DirectorDTO directorDTO) {
        Director director = directorMapper.toEntity(directorDTO);
        return directorRepository.create(director);
    }

    public Optional<DirectorDTO> findById(UUID id) {
        return directorRepository.findById(id).map(directorMapper::toDTO);
    }

    public List<DirectorDTO> findAll() {
        return directorRepository.findAll()
                .stream()
                .map(directorMapper::toDTO)
                .toList();
    }

    public boolean update(DirectorDTO directorDTO) {
        Director director = directorMapper.toEntity(directorDTO);
        return directorRepository.update(director);
    }


    public boolean delete(UUID id) {
        return directorRepository.delete(id);
    }

    @Override
    public List<DirectorDTO> findAllWithFilms() {
        return  directorRepository.findAllWithFilms().stream().map(directorMapper::toDTO).toList();
    }

    @Override
    public DirectorDTO findByIdWithFilm(UUID id) {
        Director director = directorRepository.findByIdWithFilm(id);
        return directorMapper.toDTO(director);
    }
}
