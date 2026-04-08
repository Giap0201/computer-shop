package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.product.attribute.AttributeDefinitionRequest;
import com.nguyenhuugiap.computer_shop.dto.product.attribute.AttributeDefinitionResponse;
import com.nguyenhuugiap.computer_shop.service.interfaces.AttributeDefinitionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttributeDefinitionController {
    AttributeDefinitionService attributeDefinitionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<AttributeDefinitionResponse> createAttributeDefinition(@RequestBody @Valid AttributeDefinitionRequest request) {
        return ApiResponse.<AttributeDefinitionResponse>builder()
                .result(attributeDefinitionService.createAttribute(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<AttributeDefinitionResponse>> getAllAttributeDefinitions() {
        return ApiResponse.<List<AttributeDefinitionResponse>>builder()
                .result(attributeDefinitionService.getAllAttributes())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<AttributeDefinitionResponse> getAttributeById(@PathVariable Long id) {
        return ApiResponse.<AttributeDefinitionResponse>builder()
                .result(attributeDefinitionService.getAttributeById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<AttributeDefinitionResponse> updateAttributeById(@PathVariable Long id, @RequestBody @Valid AttributeDefinitionRequest request) {
        return ApiResponse.<AttributeDefinitionResponse>builder()
                .result(attributeDefinitionService.updateAttribute(id, request))
                .build();
    }

    @GetMapping("/search")  // /attributes/search?q=RAM
    ApiResponse<AttributeDefinitionResponse> getAttributesByName(@RequestParam String name) {
        return ApiResponse.<AttributeDefinitionResponse>builder()
                .result(attributeDefinitionService.getAttributeByName(name))
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ApiResponse<Void> deleteAttributeById(@PathVariable Long id) {
        attributeDefinitionService.deleteAttributeById(id);
        return ApiResponse.<Void>builder()
                .build();
    }
}

