package com.nguyenhuugiap.computer_shop.dto.product.product_variant;

import com.nguyenhuugiap.computer_shop.dto.product.attribute.VariantAttributeValueRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProductVariantCreationRequest {
    @NotBlank(message = "SKU_CODE_INVALID")
    String skuCode;

    @NotNull(message = "PRICE_REQUIRED")
    @Min(value = 0, message = "PRICE_INVALID")
    BigDecimal price;

    @NotNull(message = "STOCK_REQUIRED")
    @Min(value = 0, message = "STOCK_INVALID")
    Long stockQuantity;

    String imageUrl;

    @NotNull(message = "VARIANT_ATTRIBUTE_VALUE_REQUIRED")
    @Valid
    List<VariantAttributeValueRequest> attributes;

}
