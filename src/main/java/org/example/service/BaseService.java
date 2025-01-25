package org.example.service;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface BaseService<T> {

    boolean create(T dto);

    Optional<T> findById(UUID id);

    List<T> findAll();

    boolean update(T dto);

    boolean delete(UUID id);
}
