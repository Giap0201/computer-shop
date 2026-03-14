package com.nguyenhuugiap.computer_shop.dto.product.product_variant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nguyenhuugiap.computer_shop.dto.product.attribute.VariantAttributeResponse;
import com.nguyenhuugiap.computer_shop.enums.VariantStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariantSlimResponse {
    Long id;
    String skuCode;
    BigDecimal price;
    Long stockQuantity;
    Long version;
    String imageUrl;
    VariantStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<VariantAttributeResponse> attributes;
}
