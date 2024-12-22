package org.example.mapper;

public interface Mapper<T,E> {

    T toDTO(E entity);
    E toEntity(T dto);
}
