package com.nguyenhuugiap.computer_shop.dto.product.product_variant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProductVariantUpdateRequest {
    @NotNull(message = "VERSION_REQUIRED")
    Long version;

    String skuCode;

    @Min(value = 0, message = "PRICE_INVALID")
    BigDecimal price;

    @Min(value = 0, message = "STOCK_INVALID")
    Long stockQuantity;

    String imageUrl;
}
