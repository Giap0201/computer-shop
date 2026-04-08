package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantResponse;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantUpdateStatusRequest;
import com.nguyenhuugiap.computer_shop.service.interfaces.ProductVariantService;
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
public class ProductVariantController {
    ProductVariantService productVariantService;

    @PostMapping("/{productId}/variants")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProductVariantResponse> createProductVariant(@PathVariable Long productId, @RequestBody @Valid ProductVariantCreationRequest request) {
        return ApiResponse.<ProductVariantResponse>builder()
                .result(productVariantService.createProductVariant(productId, request))
                .build();
    }

    @GetMapping("/{productId}/variants/{variantId}")
    ApiResponse<ProductVariantResponse> getProductVariantById(@PathVariable Long productId, @PathVariable Long variantId) {
        return ApiResponse.<ProductVariantResponse>builder()
                .result(productVariantService.getProductVariant(productId, variantId))
                .build();
    }

    @GetMapping("/{productId}/variants")
    ApiResponse<List<ProductVariantResponse>> getAllProductVariantsByProductId(@PathVariable Long productId) {
        return ApiResponse.<List<ProductVariantResponse>>builder()
                .result(productVariantService.getAllProductVariantsByProductId(productId))
                .build();
    }

    @PatchMapping("/{productId}/variants/{variantId}/status")
    ApiResponse<ProductVariantResponse> updateProductVariantByStatus(@PathVariable Long productId, @PathVariable Long variantId,
                                                                     @RequestBody ProductVariantUpdateStatusRequest status) {
        return ApiResponse.<ProductVariantResponse>builder()
                .result(productVariantService.updateProductVariantByStatus(variantId, status))
                .build();
    }

    @PutMapping("/{productId}/variants/{variantId}")
    ApiResponse<ProductVariantResponse> updateProductVariant(@PathVariable Long productId, @PathVariable Long variantId,
                                                             @RequestBody @Valid ProductVariantUpdateRequest request) {
        return ApiResponse.<ProductVariantResponse>builder()
                .result(productVariantService.updateProductVariant(variantId, request))
                .build();
    }


}
