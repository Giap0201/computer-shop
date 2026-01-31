package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.configuration.StorageProperties;
import com.nguyenhuugiap.computer_shop.exception.StorageException;
import com.nguyenhuugiap.computer_shop.service.interfaces.FileStorageService;
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
public class FileStorageServiceImpl implements FileStorageService {
    private final Path rootLocation;

    public FileStorageServiceImpl(StorageProperties storageProperties) {
        String location = storageProperties.getLocation();
        if (location == null || location.trim().isEmpty()) {
            throw new StorageException("File upload location can not be empty.");
        }

        this.rootLocation = Paths.get(location).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }

        String fileNameOriginal = file.getOriginalFilename();
        if (fileNameOriginal == null || fileNameOriginal.trim().isEmpty()) {
            throw new StorageException("Original filename is null or empty");
        }
        int lastIndex = fileNameOriginal.lastIndexOf(".");
        String extension = "";
        if (lastIndex != -1) {
            extension = fileNameOriginal.substring(lastIndex);
        }
        String newFileName = UUID.randomUUID().toString() + extension;
        Path destination = this.rootLocation.resolve(newFileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
        return newFileName;
    }
}