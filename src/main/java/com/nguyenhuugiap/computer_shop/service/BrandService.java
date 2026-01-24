package com.nguyenhuugiap.computer_shop.service;

import com.nguyenhuugiap.computer_shop.repository.BrandRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BrandService {
    BrandRepository brandRepository;

}
