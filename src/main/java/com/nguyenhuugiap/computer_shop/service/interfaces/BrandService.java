package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.request.BrandRequest;
import com.nguyenhuugiap.computer_shop.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {
    BrandResponse createBrand(BrandRequest request);
    List<BrandResponse> getAllBrands();
    BrandResponse updateBrand(Long id,BrandRequest request);
    void deleteBrand(Long id);
    BrandResponse getBrandById(Long id);
}
