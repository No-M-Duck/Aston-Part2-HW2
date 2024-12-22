package org.example.repository;

import org.apache.logging.log4j.Logger;
import org.example.LoggerUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T> {

    Logger logger = LoggerUtil.getLogger(Repository.class);


    // Создает новую запись
    boolean create(T entity);

    // Находит запись по ID
    Optional<T> findById(UUID id);

    // Возвращает все записи
    List<T> findAll();

    // Обновляет существующую запись
    boolean update(T entity);

    // Удаляет запись по ID
    boolean delete(UUID id);

    // Мапирование в объект
    T mapToEntity(ResultSet resultSet) throws SQLException;
}
