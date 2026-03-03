package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantResponse;
import com.nguyenhuugiap.computer_shop.service.interfaces.ProductVariantService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}
