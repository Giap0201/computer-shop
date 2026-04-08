package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.product.AdminProductDetailResponse;
import com.nguyenhuugiap.computer_shop.dto.product.ProductCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.ProductResponse;
import com.nguyenhuugiap.computer_shop.dto.product.UpdateProductStatusRequest;
import com.nguyenhuugiap.computer_shop.dto.product.image.ImageSlimResponse;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.VariantSlimResponse;
import com.nguyenhuugiap.computer_shop.entity.*;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.mapper.ProductImageMapper;
import com.nguyenhuugiap.computer_shop.mapper.ProductMapper;
import com.nguyenhuugiap.computer_shop.mapper.ProductVariantMapper;
import com.nguyenhuugiap.computer_shop.repository.*;
import com.nguyenhuugiap.computer_shop.service.interfaces.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;
    BrandRepository brandRepository;
    ProductVariantRepository productVariantRepository;
    ProductImageRepository productImageRepository;
    ProductVariantMapper productVariantMapper;
    ProductImageMapper productImageMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ProductResponse createProduct(ProductCreationRequest productCreationRequest) {
        Category category = categoryRepository.findById(productCreationRequest.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Brand brand = brandRepository.findById(productCreationRequest.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));

        Product product = productMapper.toProduct(productCreationRequest);
        product.setCategory(category);
        product.setBrand(brand);

        Product savedProduct = productRepository.save(product);
        return productMapper.toProductResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toProductResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProductById(long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toProductResponse(product);
    }

    @Override
    public void deleteProductById(long id) {
        if (!productRepository.existsById(id)) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public AdminProductDetailResponse getAdminProductDetail(long id) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        ProductResponse productResponse = productMapper.toProductResponse(product);

        List<ProductImage> productImages = productImageRepository.findAllByProduct_Id(product.getId());
        List<ImageSlimResponse> productImageResponses = productImageMapper.toImageSlimResponseList(productImages);

        List<ProductVariant> productVariants = productVariantRepository.findAllByProduct_Id(product.getId());
        List<VariantSlimResponse> variantSlimResponses = productVariantMapper.toVariantSlimResponseList(productVariants);

        return AdminProductDetailResponse.builder()
                .product(productResponse)
                .productImageResponse(productImageResponses)
                .productVariantResponse(variantSlimResponses)
                .build();
    }

    @Override
    public ProductResponse updateProductStatus(long id, UpdateProductStatusRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        product.setStatus(request.getProductStatus());
        return productMapper.toProductResponse(productRepository.save(product));
    }
}
