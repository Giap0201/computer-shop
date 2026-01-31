package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.configuration.StorageProperties;
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
        if (storageProperties.getLocation().trim().isEmpty()) {
            throw new RuntimeException("Storage location is empty");
        }
        this.rootLocation = Paths.get(storageProperties.getLocation());
    }


    @Override
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        String fileName = file.getOriginalFilename();
        int lastIndexOf = fileName.lastIndexOf(".");
        String extension = "";
        if (lastIndexOf != -1) {
            extension = fileName.substring(lastIndexOf);
        }
        String newFileName = UUID.randomUUID().toString() + extension;
        Path filePath = rootLocation.resolve(newFileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newFileName;
    }
}
