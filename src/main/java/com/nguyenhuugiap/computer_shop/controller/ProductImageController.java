package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.product.image.ProductImageCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.image.ProductImageResponse;
import com.nguyenhuugiap.computer_shop.dto.product.image.ProductImageUpdateRequest;
import com.nguyenhuugiap.computer_shop.service.interfaces.ProductImageService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductImageController {

    ProductImageService productImageService;

    @PostMapping("/{productId}/images")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<ProductImageResponse>> addProductImages(
            @PathVariable Long productId,
            @Valid @RequestBody List<ProductImageCreationRequest> requests) {
        return ApiResponse.<List<ProductImageResponse>>builder()
                .result(productImageService.addProductImages(productId, requests))
                .build();
    }

    @GetMapping("/{productId}/images")
    ApiResponse<List<ProductImageResponse>> getProductImages(@PathVariable Long productId) {
        return ApiResponse.<List<ProductImageResponse>>builder()
                .result(productImageService.getProductImages(productId))
                .build();
    }

    @GetMapping("/{productId}/images/{imageId}")
    ApiResponse<ProductImageResponse> getProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        return ApiResponse.<ProductImageResponse>builder()
                .result(productImageService.getProductImage(productId, imageId))
                .build();
    }

    @PutMapping("/{productId}/images")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<ProductImageResponse>> updateProductImages(
            @PathVariable Long productId,
            @Valid @RequestBody List<ProductImageUpdateRequest> requests) {
        return ApiResponse.<List<ProductImageResponse>>builder()
                .result(productImageService.updateProductImages(productId, requests))
                .build();
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Void> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        productImageService.deleteProductImage(productId, imageId);
        return ApiResponse.<Void>builder()
                .build();
    }
}