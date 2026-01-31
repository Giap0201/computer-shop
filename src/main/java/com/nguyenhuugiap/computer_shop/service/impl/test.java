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
public class test implements FileStorageService {

    private final Path rootLocation;

    public test(StorageProperties storageProperties) {
        // Lay ra vi tri se luu
        String location = storageProperties.getLocation();
        if (location == null || location.trim().isEmpty()) throw new RuntimeException("Invalid location");
        // Lấy đường dẫn path gán vào rootLoaction
        this.rootLocation = Paths.get(location).toAbsolutePath().normalize();
        // Kiểm tra xem có thư mục lưu có tên chưa, nếu chưa có thì tạo ra một thư mục mới
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String storeFile(MultipartFile file) {
        // Kiem tra xem nếu file là null thì báo lỗi
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        // Lấy ra tên ban đầu của file
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) throw new RuntimeException("Filename is empty");
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String extension = "";
        if (lastIndexOf != -1) {
            extension = originalFilename.substring(lastIndexOf);
        }
        // Tạo tên file moi lưu vào db tránh trùng lặp dữ liệu
        String newFileName = UUID.randomUUID().toString() + extension;
        Path destinationFile = rootLocation.resolve(newFileName).toAbsolutePath().normalize();
        if(!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) throw new RuntimeException("Destination file path does not match stored location");
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newFileName;
    }
}
