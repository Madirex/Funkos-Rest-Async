package com.madirex.services.crud.funko;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.madirex.exceptions.FunkoNotFoundException;
import com.madirex.exceptions.FunkoNotRemovedException;
import com.madirex.exceptions.FunkoNotSavedException;
import com.madirex.exceptions.FunkoNotValidException;
import com.madirex.models.Funko;
import com.madirex.repositories.funko.FunkoRepository;
import com.madirex.utils.LocalDateAdapter;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación de la interfaz FunkoService
 */
public class FunkoServiceImpl implements FunkoService {
    private static final int CACHE_SIZE = 25;

    @Getter
    private final Map<String, Funko> cache;
    private final Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);
    private final FunkoRepository funkoRepository;

    /**
     * Constructor de la clase
     *
     * @param funkoRepository Instancia de la clase FunkoRepository
     */
    public FunkoServiceImpl(FunkoRepository funkoRepository) {
        this.funkoRepository = funkoRepository;
        this.cache = new LinkedHashMap<>(CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Funko> eldest) {
                return size() > CACHE_SIZE;
            }
        };
    }

    /**
     * Devuelve todos los elementos del repositorio
     *
     * @return Optional de la lista de elementos
     */
    @Override
    public List<Funko> findAll() throws SQLException {
        logger.debug("Obteniendo todos los Funkos");
        return funkoRepository.findAll();
    }

    /**
     * Busca un elemento en el repositorio por su nombre
     *
     * @param name Nombre del elemento a buscar
     * @return Lista de elementos encontrados
     */
    @Override
    public List<Funko> findByName(String name) throws SQLException, FunkoNotFoundException {
        logger.debug("Obteniendo todos los Funkos ordenados por nombre");
        var list = funkoRepository.findByName(name);
        if (list.isEmpty()) {
            throw new FunkoNotFoundException("No se ha encontrado el Funko con nombre " + name);
        }
        return funkoRepository.findByName(name);
    }

    /**
     * Realiza un backup de los datos del repositorio
     *
     * @param path     Ruta del directorio donde se guardará el backup
     * @param fileName Nombre del archivo del backup
     */
    @Override
    public void backup(String path, String fileName) throws SQLException, IOException {
        File dataDir = new File(path);
        if (dataDir.exists()) {
            String dest = path + File.separator + fileName;
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .setPrettyPrinting()
                    .create();
            String json = gson.toJson(findAll());
            Files.writeString(new File(dest).toPath(), json);
            logger.debug("Backup realizado con éxito");
        } else {
            logger.error("El directorio del backup es un directorio no válido. No se creará el backup.");
        }
    }

    /**
     * Devuelve un elemento del repositorio
     *
     * @param id Id del elemento a buscar
     * @return Optional del elemento encontrado
     */
    @Override
    public Optional<Funko> findById(String id) throws SQLException {
        logger.debug("Obteniendo Funko por id");
        Funko funko = cache.get(id);
        if (funko != null) {
            logger.debug("Funko encontrado en caché");
            return Optional.of(funko);
        }
        logger.debug("Funko no encontrado en caché, buscando en base de datos");
        Optional<Funko> result = funkoRepository.findById(id);
        result.ifPresent(value -> cache.put(id, value));
        return result;
    }

    /**
     * Guarda un elemento en el repositorio
     *
     * @param funko Elemento a guardar
     * @return Optional del elemento guardado
     */
    @Override
    public Optional<Funko> save(Funko funko) throws SQLException, FunkoNotSavedException {
        logger.debug("Guardando Funko");
        cache.put(funko.getCod().toString(), funko);
        return Optional.of(funkoRepository.save(funko).orElseThrow(() ->
                new FunkoNotSavedException("No se ha podido guardar el Funko")));
    }

    /**
     * Actualiza un elemento del repositorio
     *
     * @param funkoId  Id del elemento a actualizar
     * @param newFunko Elemento con los nuevos datos
     * @return Optional del elemento actualizado
     */
    @Override
    public Optional<Funko> update(String funkoId, Funko newFunko) throws SQLException, FunkoNotValidException {
        logger.debug("Actualizando Funko");
        cache.put(newFunko.getCod().toString(), newFunko);
        return Optional.of(funkoRepository.update(funkoId, newFunko).orElseThrow(() ->
                new FunkoNotValidException("No se ha actualizado el Funko con id " + funkoId)));
    }

    /**
     * Borra un elemento del repositorio
     *
     * @param id Id del elemento a borrar
     * @return ¿Borrado?
     */
    @Override
    public boolean delete(String id) throws SQLException, FunkoNotRemovedException {
        boolean removed;
        logger.debug("Eliminando Funko");
        removed = funkoRepository.delete(id);
        if (!removed) {
            throw new FunkoNotRemovedException("No se ha encontrado el Funko con id " + id);
        }
        return removed;
    }
}