package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.product.image.ImageSlimResponse;
import com.nguyenhuugiap.computer_shop.dto.product.image.ProductImageCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.image.ProductImageResponse;
import com.nguyenhuugiap.computer_shop.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductImage toProductImage(ProductImageCreationRequest request);

    @Mapping(source = "product.id", target = "productId")
    ProductImageResponse toProductImageResponse(ProductImage productImage);

    List<ProductImageResponse> toProductImageResponseList(List<ProductImage> productImages);

    ImageSlimResponse toImageSlimResponse(ProductImage productImage);

    List<ImageSlimResponse> toImageSlimResponseList(List<ProductImage> productImages);

}
