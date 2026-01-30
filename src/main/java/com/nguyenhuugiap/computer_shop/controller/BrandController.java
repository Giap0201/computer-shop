package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.dto.request.BrandRequest;
import com.nguyenhuugiap.computer_shop.dto.response.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.response.BrandResponse;
import com.nguyenhuugiap.computer_shop.service.interfaces.BrandService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandController {
    BrandService brandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BrandResponse> createBrand(@RequestBody @Valid BrandRequest request) {
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.createBrand(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<BrandResponse>> getAllBrands() {
        return ApiResponse.<List<BrandResponse>>builder()
                .result(brandService.getAllBrands()).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> getBrandById(@PathVariable Long id) {
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.getBrandById(id)).build();
    }

    @PatchMapping("/{id}")
    public ApiResponse<BrandResponse> updateBrand(@PathVariable Long id, @RequestBody @Valid BrandRequest request) {
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.updateBrand(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ApiResponse.<Void>builder().build();
    }
}
