package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.configuration.StorageProperties;
import com.nguyenhuugiap.computer_shop.dto.response.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.response.FileResponse;
import com.nguyenhuugiap.computer_shop.service.interfaces.FileStorageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FileUploadController {
    FileStorageService fileStorageService;
    StorageProperties storageProperties;

    @PostMapping("/upload")
    public ApiResponse<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileLocation = storageProperties.getLocation();
        String newFileName = fileStorageService.storeFile(file);
        FileResponse fileResponse = FileResponse.builder()
                .fileName(newFileName)
                .uri("/" + fileLocation + "/" + newFileName)
                .build();
        return ApiResponse.<FileResponse>builder()
                .result(fileResponse)
                .build();
    }

}
