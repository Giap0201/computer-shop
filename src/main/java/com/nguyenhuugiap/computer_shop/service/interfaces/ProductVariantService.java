package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantResponse;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantUpdateStatusRequest;
import com.nguyenhuugiap.computer_shop.entity.ProductVariant;

import java.util.List;

public interface ProductVariantService {
    ProductVariantResponse createProductVariant(Long id, ProductVariantCreationRequest request);
    ProductVariantResponse getProductVariant(long productId, long variantId);
    List<ProductVariantResponse> getAllProductVariantsByProductId(long productId);
    ProductVariantResponse updateProductVariantByStatus(long id, ProductVariantUpdateStatusRequest status);
    ProductVariantResponse updateProductVariant(long id, ProductVariantUpdateRequest request);
    ProductVariant getEntityProductVariant(long id);
    ProductVariantResponse getProductVariant(Long variantId);
}
