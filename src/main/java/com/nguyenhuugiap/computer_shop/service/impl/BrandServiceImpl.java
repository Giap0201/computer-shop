package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.request.BrandCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.request.BrandUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.response.BrandResponse;
import com.nguyenhuugiap.computer_shop.entity.Brand;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.mapper.BrandMapper;
import com.nguyenhuugiap.computer_shop.repository.BrandRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.BrandService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class BrandServiceImpl implements BrandService {
    BrandRepository brandRepository;
    BrandMapper brandMapper;

    @Override
    public BrandResponse createBrand(BrandCreationRequest request) {
        if (brandRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.BRAND_EXISTS);
        return brandMapper.toResponse(brandRepository
                .save(brandMapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll().stream().map(brandMapper::toResponse).toList();
    }

    @Override
    public BrandResponse updateBrand(Long id, BrandUpdateRequest request) {
        Brand brand = brandRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.BRAND_NOT_FOUND));
        if(request.getName() != null && !request.getName().isEmpty()){
            if(brandRepository.existsByNameAndIdNot(request.getName(),id))
                throw new AppException(ErrorCode.BRAND_NAME_EXISTS);
        }
        brandMapper.toUpdate(brand, request);
        return brandMapper.toResponse(brandRepository.save(brand));
    }

    @Override
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) throw new AppException(ErrorCode.BRAND_NOT_FOUND);
        brandRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.BRAND_NOT_FOUND));
        return brandMapper.toResponse(brand);
    }
}
