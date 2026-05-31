package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.PageResponse;
import com.nguyenhuugiap.computer_shop.dto.product.*;
import com.nguyenhuugiap.computer_shop.service.interfaces.ProductService;
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
public class ProductController {
    ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductCreationRequest productCreationRequest) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(productCreationRequest))
                .build();
    }

    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> getAllProducts(@ModelAttribute ProductSearchRequest request,
                                                                     @RequestParam(defaultValue = "1") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .result(productService.getAllProducts(request, page, size))
                .build();

    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable long id) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProductById(id))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteProductById(@PathVariable long id) {
        productService.deleteProductById(id);
        return ApiResponse.<Void>builder().build();
    }

    @GetMapping("/detail/{productId}")
    ApiResponse<AdminProductDetailResponse> adminGetProductDetail(@PathVariable long productId) {
        return ApiResponse.<AdminProductDetailResponse>builder()
                .result(productService.getAdminProductDetail(productId))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{productId}/status")
    ApiResponse<ProductResponse> updateProductStatus(@PathVariable long productId, @RequestBody @Valid UpdateProductStatusRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProductStatus(productId, request))
                .build();
    }
}
