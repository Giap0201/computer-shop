package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.product.image.ProductImageCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.image.ProductImageResponse;
import com.nguyenhuugiap.computer_shop.dto.product.image.ProductImageUpdateRequest;
import com.nguyenhuugiap.computer_shop.entity.Product;
import com.nguyenhuugiap.computer_shop.entity.ProductImage;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.mapper.ProductImageMapper;
import com.nguyenhuugiap.computer_shop.repository.ProductImageRepository;
import com.nguyenhuugiap.computer_shop.repository.ProductRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.ProductImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductImageServiceImpl implements ProductImageService {
    ProductImageRepository productImageRepository;
    ProductImageMapper productImageMapper;
    ProductRepository productRepository;

    @Transactional
    @Override
    public List<ProductImageResponse> addProductImages(Long productId, List<ProductImageCreationRequest> requests) {
        if (requests.size() > 10)
            throw new AppException(ErrorCode.TOO_MANY_IMAGES);
        if (!productRepository.existsById(productId))
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);

        // 1. Tìm số thứ tự (displayOrder) lớn nhất hiện tại của sản phẩm này trong DB
        int currentMaxOrder = productImageRepository.findTopByProduct_IdOrderByDisplayOrderDesc(productId)
                .map(ProductImage::getDisplayOrder) // Nếu có ảnh, lấy displayOrder của nó
                .orElse(0);                         // Nếu chưa có ảnh nào thì coi như max = 0

        // 2. Sắp xếp list ảnh mới theo thứ tự Frontend gửi
        requests.sort(Comparator.comparingInt(ProductImageCreationRequest::getDisplayOrder));
        for (int i = 0; i < requests.size(); i++) {
            requests.get(i).setDisplayOrder(currentMaxOrder + i + 1);
        }

        Product productProxy = productRepository.getReferenceById(productId);

        List<ProductImage> productImages = requests.stream().map(request -> {
            ProductImage image = productImageMapper.toProductImage(request);
            image.setProduct(productProxy);
            return image;
        }).toList();
        List<ProductImage> savedImages = productImageRepository.saveAll(productImages);
        return productImageMapper.toProductImageResponseList(savedImages);
    }
    @Transactional(readOnly = true)
    @Override
    public List<ProductImageResponse> getProductImages(Long productId) {
        if (!productRepository.existsById(productId))
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        return productImageRepository.findAllByProduct_Id(productId).stream()
                .map(productImageMapper::toProductImageResponse).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ProductImageResponse getProductImage(Long productId, Long imageId) {
        ProductImage productImage = productImageRepository.findByIdAndProduct_Id(imageId, productId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));
        return productImageMapper.toProductImageResponse(productImage);
    }

    @Transactional
    @Override
    public void deleteProductImage(Long productId, Long imageId) {
        ProductImage productImage = productImageRepository.findByIdAndProduct_Id(imageId, productId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));
        productImageRepository.delete(productImage);
    }

    @Transactional
    @Override
    public List<ProductImageResponse> updateProductImages(Long productId, List<ProductImageUpdateRequest> requests) {
        requests.sort(Comparator.comparingInt(ProductImageUpdateRequest::getDisplayOrder));
        for (int i = 0; i < requests.size(); i++) {
            requests.get(i).setDisplayOrder(i + 1);
        }
        List<ProductImage> existImages = productImageRepository.findAllByProduct_Id(productId);
        Map<Long, ProductImage> existingImageMap = existImages.stream()
                .collect(Collectors.toMap(ProductImage::getId, img -> img));

        List<ProductImage> imagesToSave = new ArrayList<>();
        Set<Long> requestIds = new HashSet<>();

        Product productProxy = productRepository.getReferenceById(productId);
        for (ProductImageUpdateRequest request : requests) {
            if (request.getId() != null) {
                //Truong hop anh cu trong db
                ProductImage image = existingImageMap.get(request.getId());
                if (image != null) {
                    image.setImageUrl(request.getImageUrl());
                    image.setDisplayOrder(request.getDisplayOrder());
                    imagesToSave.add(image);
                    requestIds.add(request.getId());
                } else {
                    throw new AppException(ErrorCode.IMAGE_NOT_FOUND);
                }
            } else {
                // Truong hop anh moi toanh, fe khong gui id
                ProductImage image = ProductImage.builder()
                        .imageUrl(request.getImageUrl())
                        .displayOrder(request.getDisplayOrder())
                        .product(productProxy)
                        .build();
                imagesToSave.add(image);
            }
        }
        // Truong hop tim anh bi xoa, db co ma fe khong gui len
        List<ProductImage> imagesToDelete = existImages.stream()
                .filter(img -> !requestIds.contains(img.getId())).toList();

        if (!imagesToDelete.isEmpty()) {
            productImageRepository.deleteAllInBatch(imagesToDelete);
        }
        List<ProductImage> savedImages = productImageRepository.saveAll(imagesToSave);
        return productImageMapper.toProductImageResponseList(savedImages);
    }
}
