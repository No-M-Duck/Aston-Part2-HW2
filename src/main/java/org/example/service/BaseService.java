package org.example.service;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface BaseService<T> {

    boolean createEntity(T dto);

    Optional<T> findEntityById(UUID id);

    List<T> findAllEntity();

    boolean updateEntity(T dto);

    boolean deleteEntity(UUID id);
}
