package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.product.ProductCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.ProductResponse;
import com.nguyenhuugiap.computer_shop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "minPrice", ignore = true)
    @Mapping(target = "productVariants", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    Product toProduct(ProductCreationRequest productCreationRequest);

    ProductResponse toProductResponse(Product product);
}
