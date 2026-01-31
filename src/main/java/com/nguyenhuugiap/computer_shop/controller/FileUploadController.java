package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.dto.response.ApiResponse;
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

    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<String>builder()
                .result(fileStorageService.storeFile(file))
                .build();
    }

}
