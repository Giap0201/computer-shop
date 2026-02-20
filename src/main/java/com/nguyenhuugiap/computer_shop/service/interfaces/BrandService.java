package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.request.brand.BrandCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.request.brand.BrandUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {
    BrandResponse createBrand(BrandCreationRequest request);
    List<BrandResponse> getAllBrands();
    BrandResponse updateBrand(Long id, BrandUpdateRequest request);
    void deleteBrand(Long id);
    BrandResponse getBrandById(Long id);
}
