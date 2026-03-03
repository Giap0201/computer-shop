package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantResponse;

public interface ProductVariantService {
    ProductVariantResponse createProductVariant(Long id, ProductVariantCreationRequest request);
}
