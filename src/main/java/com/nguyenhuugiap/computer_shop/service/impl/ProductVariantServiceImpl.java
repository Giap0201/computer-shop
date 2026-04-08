package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.product.attribute.VariantAttributeValueRequest;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantResponse;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.ProductVariantUpdateStatusRequest;
import com.nguyenhuugiap.computer_shop.entity.AttributeDefinition;
import com.nguyenhuugiap.computer_shop.entity.Product;
import com.nguyenhuugiap.computer_shop.entity.ProductVariant;
import com.nguyenhuugiap.computer_shop.entity.VariantAttributeValue;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.mapper.ProductVariantMapper;
import com.nguyenhuugiap.computer_shop.repository.AttributeDefinitionRepository;
import com.nguyenhuugiap.computer_shop.repository.ProductRepository;
import com.nguyenhuugiap.computer_shop.repository.ProductVariantRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.ProductVariantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Log4j2
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProductVariantServiceImpl implements ProductVariantService {
    ProductVariantRepository productVariantRepository;
    ProductRepository productRepository;
    AttributeDefinitionRepository attributeDefinitionRepository;
    ProductVariantMapper productVariantMapper;

    @Override
    public ProductVariantResponse createProductVariant(Long id, ProductVariantCreationRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (productVariantRepository.existsBySkuCode(request.getSkuCode()))
            throw new AppException(ErrorCode.SKU_CODE_EXISTS);

        ProductVariant productVariant = productVariantMapper.toProductVariant(request);
        productVariant.setProduct(product);
        List<Long> attributeIds = request.getAttributes().stream()
                .map(VariantAttributeValueRequest::getAttributeId)
                .toList();
        List<AttributeDefinition> validAttributes = attributeDefinitionRepository.findAllById(attributeIds);

        if (validAttributes.size() != attributeIds.size()) {
            throw new AppException(ErrorCode.ATTRIBUTE_NOT_FOUND);
        }
        Map<Long, AttributeDefinition> attributeMap = validAttributes.stream()
                .collect(Collectors.toMap(AttributeDefinition::getId, attr -> attr));

        request.getAttributes().forEach(attributeReq -> {
            AttributeDefinition attributeDef = attributeMap.get(attributeReq.getAttributeId());

            VariantAttributeValue variantAttributeValue = VariantAttributeValue.builder()
                    .value(attributeReq.getValue())
                    .attributeDefinition(attributeDef)
                    .build();

            productVariant.addAttributeValue(variantAttributeValue);
        });

        if (product.getMinPrice() == null || request.getPrice().compareTo(product.getMinPrice()) < 0) {
            product.setMinPrice(request.getPrice());
        }

        ProductVariant savedProductVariant = productVariantRepository.save(productVariant);
        return productVariantMapper.toProductVariantResponse(savedProductVariant);
    }

    @Override
    public ProductVariantResponse getProductVariant(long productId, long variantId) {
        ProductVariant productVariant = productVariantRepository.findByIdAndProductId(variantId, productId).orElseThrow(() ->
                new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));
        return productVariantMapper.toProductVariantResponse(productVariant);
    }

    @Override
    public List<ProductVariantResponse> getAllProductVariantsByProductId(long productId) {
        return productVariantRepository.findAllByProduct_Id(productId).stream()
                .map(productVariantMapper::toProductVariantResponse).toList();
    }

    @Override
    public ProductVariantResponse updateProductVariantByStatus(long id, ProductVariantUpdateStatusRequest status) {
        ProductVariant productVariant = productVariantRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));
        productVariant.setStatus(status.getStatus());
        return productVariantMapper.toProductVariantResponse(productVariantRepository.save(productVariant));
    }

    @Override
    public ProductVariantResponse updateProductVariant(long id, ProductVariantUpdateRequest request) {
        ProductVariant productVariant = productVariantRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));
        if (!productVariant.getVersion().equals(request.getVersion())) {
            throw new AppException(ErrorCode.VERSION_MISMATCH);
        }
        if (request.getSkuCode() != null
                && !request.getSkuCode().equals(productVariant.getSkuCode())
                && productVariantRepository.existsBySkuCode(request.getSkuCode())) {
            throw new AppException(ErrorCode.SKU_CODE_EXISTS);
        }
        if (productVariant.getProduct().getMinPrice() == null ||
                request.getPrice().compareTo(productVariant.getProduct().getMinPrice()) < 0) {
            productVariant.getProduct().setMinPrice(request.getPrice());
        }
        productVariantMapper.updateProductVariant(productVariant, request);
        ProductVariant updatedVariant = productVariantRepository.save(productVariant);
        return productVariantMapper.toProductVariantResponse(updatedVariant);
    }

    @Override
    public ProductVariant getEntityProductVariant(long id) {
        return productVariantRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));
    }

    @Override
    public ProductVariantResponse getProductVariant(Long variantId) {
        ProductVariant productVariant = getEntityProductVariant(variantId);
        return productVariantMapper.toProductVariantResponse(productVariant);
    }
}
