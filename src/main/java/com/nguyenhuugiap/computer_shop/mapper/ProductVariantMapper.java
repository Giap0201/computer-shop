package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantResponse;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.VariantSlimResponse;
import com.nguyenhuugiap.computer_shop.entity.ProductVariant;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {VariantAttributeValueMapper.class})
public interface ProductVariantMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "attributeValues", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductVariant toProductVariant(ProductVariantCreationRequest request);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "attributeValues", target = "attributes")
    ProductVariantResponse toProductVariantResponse(ProductVariant productVariant);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductVariant(@MappingTarget ProductVariant productVariant, ProductVariantUpdateRequest request);

    // 2. Dùng cho API lấy Product Detail
    @Mapping(source = "attributeValues", target = "attributes")
    VariantSlimResponse toVariantSlimResponse(ProductVariant productVariant);

    List<VariantSlimResponse> toVariantSlimResponseList(List<ProductVariant> variants);

}
