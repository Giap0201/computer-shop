package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.product.attribute.AttributeDefinitionRequest;
import com.nguyenhuugiap.computer_shop.dto.product.attribute.AttributeDefinitionResponse;
import com.nguyenhuugiap.computer_shop.entity.AttributeDefinition;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.mapper.AttributeDefinitionMapper;
import com.nguyenhuugiap.computer_shop.repository.AttributeDefinitionRepository;
import com.nguyenhuugiap.computer_shop.repository.VariantAttributeValueRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.AttributeDefinitionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class AttributeDefinitionImpl implements AttributeDefinitionService {

    AttributeDefinitionRepository attributeDefinitionRepository;
    AttributeDefinitionMapper attributeDefinitionMapper;
    VariantAttributeValueRepository variantAttributeValueRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public AttributeDefinitionResponse createAttribute(AttributeDefinitionRequest attributeDefinitionRequest) {
        String name = attributeDefinitionRequest.getName().trim().toUpperCase();
        if (attributeDefinitionRepository.existsByName(name))
            throw new AppException(ErrorCode.ATTRIBUTE_EXISTS);
        AttributeDefinition attributeDefinition = attributeDefinitionMapper.toAttributeDefinition(attributeDefinitionRequest);
        attributeDefinition.setName(name);
        AttributeDefinition savedAttributeDefinition = attributeDefinitionRepository.save(attributeDefinition);
        return attributeDefinitionMapper.toAttributeDefinitionResponse(savedAttributeDefinition);
    }


    @Transactional(readOnly = true)
    @Override
    public AttributeDefinitionResponse getAttributeById(Long id) {
        AttributeDefinition attributeDefinition = attributeDefinitionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_NOT_FOUND));
        return attributeDefinitionMapper.toAttributeDefinitionResponse(attributeDefinition);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public AttributeDefinitionResponse updateAttribute(Long id, AttributeDefinitionRequest attributeDefinitionRequest) {
        AttributeDefinition attributeDefinition = attributeDefinitionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_NOT_FOUND));
        String newName = attributeDefinitionRequest.getName().trim().toUpperCase();
        if (!attributeDefinition.getName().equals(newName) &&
                attributeDefinitionRepository.existsByName(newName))
            throw new AppException(ErrorCode.ATTRIBUTE_EXISTS);
        attributeDefinitionMapper.toUpdateAttributeDefinition(attributeDefinition, attributeDefinitionRequest);
        attributeDefinition.setName(newName);
        return attributeDefinitionMapper.toAttributeDefinitionResponse
                (attributeDefinitionRepository.save(attributeDefinition));
    }

    @Transactional(readOnly = true)
    @Override
    public AttributeDefinitionResponse getAttributeByName(String name) {
        String attributeName = name.trim().toUpperCase();
        AttributeDefinition attributeDefinition = attributeDefinitionRepository.findByName(attributeName)
                .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_NOT_FOUND));
        return attributeDefinitionMapper.toAttributeDefinitionResponse(attributeDefinition);
    }


    @Transactional(readOnly = true)
    @Override
    public List<AttributeDefinitionResponse> getAllAttributes() {
        return attributeDefinitionRepository.findAll().stream()
                .map(attributeDefinitionMapper::toAttributeDefinitionResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteAttributeById(Long id) {
        if (!attributeDefinitionRepository.existsById(id))
            throw new AppException(ErrorCode.ATTRIBUTE_NOT_FOUND);
        if (variantAttributeValueRepository.existsByAttributeDefinitionId(id))
            throw new AppException(ErrorCode.ATTRIBUTE_IN_USE);
        attributeDefinitionRepository.deleteById(id);
    }
}
