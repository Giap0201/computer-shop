package com.nguyenhuugiap.computer_shop.service;

import com.nguyenhuugiap.computer_shop.dto.request.BrandRequest;
import com.nguyenhuugiap.computer_shop.dto.response.BrandResponse;
import com.nguyenhuugiap.computer_shop.entity.Brand;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.mapper.BrandMapper;
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
    BrandMapper brandMapper;
    public BrandResponse createBrand(BrandRequest brandRequest) {
        if(brandRepository.existsByName(brandRequest.getName())) throw new AppException(ErrorCode.BRAND_EXISTS);
        Brand brand = brandMapper.toEntity(brandRequest);
        return brandMapper.toResponse(brandRepository.save(brand));
    }
}
