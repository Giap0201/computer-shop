package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.product.attribute.AttributeDefinitionRequest;
import com.nguyenhuugiap.computer_shop.dto.product.attribute.AttributeDefinitionResponse;
import com.nguyenhuugiap.computer_shop.service.interfaces.AttributeDefinitionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class AttributeDefinitionImpl implements AttributeDefinitionService {
    @Override
    public AttributeDefinitionResponse createAttribute(AttributeDefinitionRequest attributeDefinitionRequest) {
        return null;
    }

    @Override
    public AttributeDefinitionResponse getAttributeById(Long id) {
        return null;
    }

    @Override
    public AttributeDefinitionResponse updateAttribute(Long id, AttributeDefinitionRequest attributeDefinitionRequest) {
        return null;
    }

    @Override
    public AttributeDefinitionResponse getAttributeByName(String name) {
        return null;
    }

    @Override
    public List<AttributeDefinitionResponse> getAllAttributes() {
        return List.of();
    }

    @Override
    public void deleteAttributeById(Long id) {

    }
}
