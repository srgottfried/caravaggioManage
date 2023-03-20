package com.proyud4.model.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Implementación de la interfaz `IUploadFileService`
 */
@Service
public class UploadFileService implements IUploadFileService {

    private final static String UPLOAD_FOLDER = "uploads";
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Carga el recurso cuyo nombre es pasado como parámetro.
     * @param filename
     * @return recurso
     * @throws MalformedURLException
     */
    @Override
    public Resource load(String filename) throws MalformedURLException {
        Path pathFoto = getPath(filename);
        Resource recurso;

        recurso = new UrlResource(pathFoto.toUri());
        if (!recurso.exists() || !recurso.isReadable()) {
            throw new RuntimeException("Error: no se puede cargar la imagen: " + pathFoto);
        }

        return recurso;
    }

    /**
     * Copia en el directorio del servidor el fichero pasado como parámetro modificando previamente
     * su nombre para hacerlo único ssando `UUID.randomUUID()`.
     * @param file cargado
     * @return el nombre único del fichero almacenado
     * @throws IOException
     */
    @Override
    public String copy(MultipartFile file) throws IOException {
        String uniqueFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path rootPath = getPath(uniqueFilename);

        Files.copy(file.getInputStream(), rootPath);

        return uniqueFilename;
    }

    /**
     * Borra el fichero almacenado cuyo nombre es pasado como parámetro.
     * @param filename a borrar
     * @return si hay éxito
     */
    @Override
    public boolean delete(String filename) {
        Path rootPath = getPath(filename);
        File archivo = rootPath.toFile();

        if (archivo.exists() && archivo.canRead()) {
            if (archivo.delete()) {
                return true;
            }
        }
        return true;
    }

    /**
     * Borra recursivamente el directorio de almacenamiento de ficheros.
     */
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(Path.of(UPLOAD_FOLDER).toFile());
    }

    /**
     * Crea el directorio de almacenamiento de ficheros.
     * @throws IOException
     */
    @Override
    public void init() throws IOException {
        Files.createDirectory(Path.of(UPLOAD_FOLDER));
    }

    /**
     * Obtiene el path absoluto asociado al nombre de fichero pasado por parámetro.
     * @param filename
     * @return
     */
    public Path getPath(String filename) {
        return Path.of(UPLOAD_FOLDER).resolve(filename).toAbsolutePath();
    }
}
