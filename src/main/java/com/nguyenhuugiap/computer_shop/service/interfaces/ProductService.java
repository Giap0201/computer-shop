package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.PageResponse;
import com.nguyenhuugiap.computer_shop.dto.product.*;

public interface ProductService {
    ProductResponse createProduct(ProductCreationRequest productCreationRequest);

    PageResponse<ProductResponse> getAllProducts(ProductSearchRequest request, int page, int size);

    ProductResponse getProductById(long id);

    void deleteProductById(long id);

    AdminProductDetailResponse getAdminProductDetail(long id);

    ProductResponse updateProductStatus(long id, UpdateProductStatusRequest request);
}
