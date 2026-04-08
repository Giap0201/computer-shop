package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.product.attribute.VariantAttributeResponse;
import com.nguyenhuugiap.computer_shop.entity.VariantAttributeValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VariantAttributeValueMapper {
    @Mapping(source = "attributeDefinition.id", target = "attributeId")
    @Mapping(source = "attributeDefinition.name", target = "attributeName")
    VariantAttributeResponse toVariantAttributeResponse(VariantAttributeValue variantAttributeValue);
}
