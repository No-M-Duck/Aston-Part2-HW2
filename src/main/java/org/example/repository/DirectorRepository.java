package org.example.repository;

import org.example.entity.Director;

import java.util.List;
import java.util.UUID;

public interface DirectorRepository extends Repository<Director>{

    List<Director> findAllWithFilms();
    
    Director findByIdWithFilm(UUID id);

}
