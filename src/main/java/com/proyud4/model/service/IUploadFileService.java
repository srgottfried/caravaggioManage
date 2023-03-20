package com.proyud4.model.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Servicio de subida de ficheros al sitio. Mantiene los ficheros
 * en una carpeta independiente del proyecto.
 */
public interface IUploadFileService {

    Resource load(String filename) throws MalformedURLException;

    String copy(MultipartFile file) throws IOException;

    boolean delete(String file);

    void deleteAll();

    void init() throws IOException;
}
