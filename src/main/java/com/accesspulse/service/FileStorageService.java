package com.accesspulse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadLocation;

    public FileStorageService(@Value("${accesspulse.upload.directory:uploads}") String uploadDir) {
        this.uploadLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory!", e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = this.uploadLocation.resolve(uniqueFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }
            return uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + uniqueFilename, e);
        }
    }

    public void deleteFile(String filename) {
        if (filename == null || filename.isBlank()) {
            return;
        }
        try {
            Path targetLocation = this.uploadLocation.resolve(filename);
            Files.deleteIfExists(targetLocation);
        } catch (IOException ignored) {
        }
    }
}
