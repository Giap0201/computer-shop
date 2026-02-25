package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.product.ProductCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(ProductCreationRequest productCreationRequest);
}
