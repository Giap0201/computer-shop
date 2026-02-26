package com.nguyenhuugiap.computer_shop.mapper;


import com.nguyenhuugiap.computer_shop.dto.product.attribute.AttributeDefinitionRequest;
import com.nguyenhuugiap.computer_shop.dto.product.attribute.AttributeDefinitionResponse;
import com.nguyenhuugiap.computer_shop.entity.AttributeDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttributeDefinitionMapper {
    @Mapping(target = "id", ignore = true)
    AttributeDefinition toAttributeDefinition(AttributeDefinitionRequest attributeDefinitionRequest);

    AttributeDefinitionResponse toAttributeDefinitionResponse(AttributeDefinition attributeDefinition);

}
