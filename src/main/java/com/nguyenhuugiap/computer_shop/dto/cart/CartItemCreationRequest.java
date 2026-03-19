package com.nguyenhuugiap.computer_shop.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemCreationRequest {
    @NotNull(message = "PRODUCT_VARIANT_REQUIRED")
    Long productVariantId;

    @NotNull(message = "QUANTITY_REQUIRED")
    @Min(value = 0, message = "QUANTITY_INVALID")
    Integer quantity;
}
