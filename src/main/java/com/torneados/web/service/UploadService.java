package com.torneados.web.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class UploadService {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public String guardarImagen(MultipartFile archivo, String nombreArchivo) throws IOException {
        String extension = Optional.ofNullable(archivo.getOriginalFilename())
            .filter(f -> f.contains("."))
            .map(f -> f.substring(archivo.getOriginalFilename().lastIndexOf(".")))
            .orElse("");

        String nombreFinal = nombreArchivo + extension;

        Path rutaDestino = Paths.get(UPLOAD_DIR).resolve(nombreFinal).normalize();
        Files.copy(archivo.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + nombreFinal;
    }
}
