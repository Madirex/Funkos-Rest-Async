package com.madirex.services.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.madirex.exceptions.DirectoryException;
import com.madirex.utils.LocalDateAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;

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
     * Realiza un backup de los datos
     *
     * @param path     Ruta del directorio donde se guardará el backup
     * @param fileName Nombre del archivo del backup
     * @param data     Datos a guardar
     * @throws IOException Si hay un error al guardar el archivo
     */
    public void backup(String path, String fileName, T data) throws IOException, DirectoryException {
        File dataDir = new File(path);
        if (dataDir.exists()) {
            String dest = path + File.separator + fileName;
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .setPrettyPrinting()
                    .create();
            String json = gson.toJson(data);
            Files.writeString(new File(dest).toPath(), json);
            logger.debug("Backup realizado con éxito");
        } else {
            throw new DirectoryException("No se creará el backup.");
        }
    }

}
