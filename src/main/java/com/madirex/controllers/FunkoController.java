package com.madirex.controllers;

import com.madirex.exceptions.FunkoNotFoundException;
import com.madirex.exceptions.FunkoNotRemovedException;
import com.madirex.exceptions.FunkoNotSavedException;
import com.madirex.exceptions.FunkoNotValidException;
import com.madirex.models.Funko;
import com.madirex.services.crud.funko.FunkoService;
import com.madirex.validators.FunkoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador de Funko
 */
public class FunkoController implements BaseController<Funko> {
    private final Logger logger = LoggerFactory.getLogger(FunkoController.class);

    private final FunkoService funkoService;

    /**
     * Constructor
     *
     * @param funkoService servicio de Funko
     */
    public FunkoController(FunkoService funkoService) {
        this.funkoService = funkoService;
    }

    /**
     * Busca todos los Funkos
     *
     * @return Funkos encontrados
     * @throws SQLException           si hay un error en la base de datos
     * @throws FunkoNotFoundException si no se encuentran Funkos
     */
    public List<Funko> findAll() throws SQLException, FunkoNotFoundException {
        logger.debug("FindAll");
        return funkoService.findAll();
    }

    /**
     * Busca un Funko por id
     *
     * @param id id del Funko
     * @return Funko encontrado
     * @throws SQLException           si hay un error en la base de datos
     * @throws FunkoNotFoundException si no se encuentra el Funko
     */
    public Optional<Funko> findById(String id) throws SQLException, FunkoNotFoundException {
        String msg = "FindById " + id;
        logger.debug(msg);
        return funkoService.findById(id);
    }

    /**
     * Busca Funkos por nombre
     *
     * @param name nombre del Funko
     * @return Funkos encontrados
     * @throws SQLException           si hay un error en la base de datos
     * @throws FunkoNotFoundException si no se encuentra el Funko
     */
    public List<Funko> findByName(String name) throws SQLException, FunkoNotFoundException {
        String msg = "FindByName " + name;
        logger.debug(msg);
        return funkoService.findByName(name);
    }

    /**
     * Guarda un Funko
     *
     * @param funko Funko a guardar
     * @return Funko guardado
     * @throws SQLException           si hay un error en la base de datos
     * @throws FunkoNotSavedException si no se guarda el Funko
     * @throws FunkoNotValidException si el Funko no es válido
     */
    public Optional<Funko> save(Funko funko) throws SQLException, FunkoNotSavedException, FunkoNotValidException {
        String msg = "Save " + funko;
        logger.debug(msg);
        FunkoValidator.validate(funko);
        return funkoService.save(funko);
    }

    /**
     * Actualiza un Funko
     *
     * @param id    id del Funko
     * @param funko Funko a actualizar
     * @return Funko actualizado
     * @throws FunkoNotValidException si el Funko no es válido
     * @throws SQLException           si hay un error en la base de datos
     */
    public Optional<Funko> update(String id, Funko funko) throws FunkoNotValidException, SQLException {
        String msg = "Update " + funko;
        logger.debug(msg);
        FunkoValidator.validate(funko);
        return funkoService.update(id, funko);
    }

    /**
     * Elimina un Funko
     *
     * @param id id del Funko
     * @return Funko eliminado
     * @throws SQLException             si hay un error en la base de datos
     * @throws FunkoNotFoundException   si no se encuentra el Funko
     * @throws FunkoNotRemovedException si no se elimina el Funko
     */
    public Optional<Funko> delete(String id) throws SQLException, FunkoNotFoundException, FunkoNotRemovedException {
        String msg = "Delete " + id;
        boolean removed = false;
        logger.debug(msg);
        var funko = funkoService.findById(id);
        if (funko.isPresent()) {
            removed = funkoService.delete(id);
        }
        if (!removed) {
            return Optional.empty();
        }
        return funko;
    }

    /**
     * Realiza un backup de la base de datos
     *
     * @param url      url de la base de datos
     * @param fileName nombre del archivo
     * @throws SQLException si hay un error en la base de datos
     * @throws IOException  si hay un error en el archivo
     */
    public void backup(String url, String fileName) throws SQLException, IOException {
        funkoService.backup(url, fileName);
    }
}