package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.product.image.ProductImageCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.image.ProductImageResponse;
import com.nguyenhuugiap.computer_shop.dto.product.image.ProductImageUpdateRequest;

import java.util.List;

public interface ProductImageService {
    List<ProductImageResponse> addProductImages(Long productId, List<ProductImageCreationRequest> requests);
    List<ProductImageResponse> getProductImages(Long productId);
    ProductImageResponse getProductImage(Long productId, Long imageId);
    void deleteProductImage(Long productId, Long imageId);
    List<ProductImageResponse> updateProductImages(Long productId, List<ProductImageUpdateRequest> requests);
}
