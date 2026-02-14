package com.nguyenhuugiap.computer_shop.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file);
}
