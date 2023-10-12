package com.madirex.services.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.madirex.exceptions.DirectoryException;
import com.madirex.exceptions.ExportDataException;
import com.madirex.exceptions.ImportDataException;
import com.madirex.utils.LocalDateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Clase BackupService
 */
public class BackupService<T> {

    private static BackupService backupServiceInstance;
    private final Logger logger = LoggerFactory.getLogger(BackupService.class);

    /**
     * Constructor de la clase
     */
    private BackupService() {
    }

    /**
     * Devuelve la instancia de la clase
     *
     * @return Instancia de la clase
     */
    public static synchronized BackupService getInstance() {
        if (backupServiceInstance == null) {
            backupServiceInstance = new BackupService();
        }
        return backupServiceInstance;
    }

    /**
     * Exportar los datos pasados por parámetro a un archivo JSON
     *
     * @param path     Ruta del directorio donde se guardará el backup
     * @param fileName Nombre del archivo del backup
     * @param data     Datos a guardar
     */
    public void exportData(String path, String fileName, T data) {
        CompletableFuture.runAsync(() -> {
            File dataDir = new File(path);
            if (dataDir.exists()) {
                String dest = path + File.separator + fileName;
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                        .setPrettyPrinting()
                        .create();
                String json = gson.toJson(data);
                try {
                    Files.writeString(new File(dest).toPath(), json);
                } catch (IOException e) {
                    throw new CompletionException(new ExportDataException(e.getMessage()));
                }
                logger.debug("Backup realizado con éxito");
            } else {
                throw new CompletionException(new DirectoryException("No se creará el backup."));
            }
        });
    }

    /**
     * Importa los datos de un archivo JSON
     *
     * @param path     Ruta del archivo JSON
     * @param fileName Nombre del archivo JSON
     * @param dataType Tipo de dato a importar
     * @return CompletableFuture de los datos importados
     */
    public CompletableFuture<T> importData(String path, String fileName, Class<T> dataType) {
        return CompletableFuture.supplyAsync(() -> {
            File dataDir = new File(path);
            try {
                if (dataDir.exists()) {
                    String dest = path + File.separator + fileName;
                    String json = new String(Files.readAllBytes(Paths.get(dest)));
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                            .create();
                    return gson.fromJson(json, dataType);
                } else {
                    throw new CompletionException(new DirectoryException("No se creará el backup."));
                }
            } catch (IOException | RuntimeException e) {
                throw new CompletionException(new ImportDataException(e.getMessage()));
            }
        }).exceptionally(ex -> {
            throw new CompletionException(new ImportDataException(ex.getMessage()));
        });
    }
}
