package com.madirex.services.crud;

import com.madirex.exceptions.FunkoNotFoundException;
import com.madirex.exceptions.FunkoNotRemovedException;
import com.madirex.exceptions.FunkoNotSavedException;
import com.madirex.exceptions.FunkoNotValidException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones CRUD de BaseCRUDService
 */
public interface BaseCRUDService<I, E extends Throwable> {
    List<I> findAll() throws SQLException;

    Optional<I> findById(String id) throws SQLException, FunkoNotFoundException;

    Optional<I> save(I item) throws SQLException, FunkoNotSavedException;

    Optional<I> update(String id, I newI) throws SQLException, FunkoNotValidException;

    boolean delete(String id) throws SQLException, FunkoNotRemovedException;
}
