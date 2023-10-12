package com.madirex.controllers;

import com.madirex.exceptions.FunkoException;
import com.madirex.exceptions.FunkoNotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador base
 *
 * @param <T> Entity
 */
public interface BaseController<T> {
    List<T> findAll() throws SQLException, FunkoNotFoundException;

    Optional<T> findById(String id) throws SQLException, FunkoNotFoundException;

    List<T> findByName(String name) throws SQLException, FunkoNotFoundException;

    Optional<T> save(T entity) throws SQLException, FunkoException;

    Optional<T> update(String id, T entity) throws SQLException, FunkoException;

    Optional<T> delete(String id) throws SQLException, FunkoException;
}
