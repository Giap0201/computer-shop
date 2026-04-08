package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.product.AdminProductDetailResponse;
import com.nguyenhuugiap.computer_shop.dto.product.ProductCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.ProductResponse;
import com.nguyenhuugiap.computer_shop.dto.product.UpdateProductStatusRequest;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductCreationRequest productCreationRequest);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(long id);

    void deleteProductById(long id);

    AdminProductDetailResponse getAdminProductDetail(long id);

    ProductResponse updateProductStatus(long id, UpdateProductStatusRequest request);
}
