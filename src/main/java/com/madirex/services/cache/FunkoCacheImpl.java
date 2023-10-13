package com.madirex.services.cache;

import com.madirex.controllers.FunkoController;
import com.madirex.models.Funko;
import com.madirex.repositories.funko.FunkoRepositoryImpl;
import com.madirex.services.crud.funko.FunkoServiceImpl;
import com.madirex.services.crud.funko.IdGenerator;
import com.madirex.services.database.DatabaseManager;
import com.madirex.services.io.BackupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementación de la interfaz FunkoCache
 */
public class FunkoCacheImpl implements FunkoCache {
    private final Logger logger = LoggerFactory.getLogger(FunkoCacheImpl.class);
    private final int maxSize;
    private final Map<String, Funko> cache;
    private final ScheduledExecutorService cleaner;


    /**
     * Constructor de la clase
     *
     * @param maxSize tamaño máximo de la caché
     */
    public FunkoCacheImpl(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<String, Funko>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Funko> eldest) {
                return size() > maxSize;
            }
        };
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::clear, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Asigna un Funko a la caché
     *
     * @param key   Id
     * @param value Funko
     */
    @Override
    public void put(String key, Funko value) {
        String str = "Añadiendo Funko a caché con ID: " + key + " y valor: " + value;
        logger.debug(str);
        cache.put(key, value);
    }

    /**
     * Devuelve el Funko de la caché
     *
     * @param key Id
     * @return Funko
     */
    @Override
    public Funko get(String key) {
        String str = "Obteniendo Funko de caché con ID: " + key;
        logger.debug(str);
        return cache.get(key);
    }

    /**
     * Elimina el Funko de la caché
     *
     * @param key Id
     */
    @Override
    public void remove(String key) {
        String str = "Eliminando Funko de caché con ID: " + key;
        logger.debug(str);
        cache.remove(key);
    }

    /**
     * Elimina los Funkos de la caché que hayan caducado si llevan más de 2 minutos sin ser modificados
     */
    @Override
    public void clear() {
        cache.entrySet().removeIf(entry -> {
            FunkoController controller = FunkoController.getInstance(FunkoServiceImpl
                    .getInstance(FunkoRepositoryImpl.getInstance(IdGenerator.getInstance(), DatabaseManager.getInstance()),
                            new FunkoCacheImpl(10),
                            BackupService.getInstance()));
            controller.findUpdateAtByFunkoId(entry.getKey()).thenApplyAsync(a -> {
                boolean shouldRemove = a.plusMinutes(2).isBefore(LocalDateTime.now());
                if (shouldRemove) {
                    String str = "Autoeliminando por caducidad Funko de caché con ID: " + entry.getKey();
                    logger.debug(str);
                }
                return shouldRemove;
            });
            return false;
        });
    }

    /**
     * Elimina todos los Funkos de la caché
     */
    @Override
    public void shutdown() {
        cleaner.shutdown();
    }
}
