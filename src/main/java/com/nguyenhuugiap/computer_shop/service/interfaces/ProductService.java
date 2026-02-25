package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.product.ProductCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductCreationRequest productCreationRequest);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(long id);
    void deleteProductById(long id);
}
