package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.product.ProductCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.ProductResponse;
import com.nguyenhuugiap.computer_shop.entity.Brand;
import com.nguyenhuugiap.computer_shop.entity.Category;
import com.nguyenhuugiap.computer_shop.entity.Product;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.mapper.ProductMapper;
import com.nguyenhuugiap.computer_shop.repository.BrandRepository;
import com.nguyenhuugiap.computer_shop.repository.CategoryRepository;
import com.nguyenhuugiap.computer_shop.repository.ProductRepository;
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
}
