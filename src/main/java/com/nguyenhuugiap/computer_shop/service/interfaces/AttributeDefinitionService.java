package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.product.attribute.AttributeDefinitionRequest;
import com.nguyenhuugiap.computer_shop.dto.product.attribute.AttributeDefinitionResponse;

import java.util.List;

public interface AttributeDefinitionService {
    AttributeDefinitionResponse createAttribute(AttributeDefinitionRequest attributeDefinitionRequest);
    AttributeDefinitionResponse getAttributeById(Long id);
    AttributeDefinitionResponse updateAttribute(Long id, AttributeDefinitionRequest attributeDefinitionRequest);
    AttributeDefinitionResponse getAttributeByName(String name);
    List<AttributeDefinitionResponse> getAllAttributes();
    void deleteAttributeById(Long id);
}
