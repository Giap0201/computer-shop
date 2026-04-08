package com.nguyenhuugiap.computer_shop.dto.product.product_variant;

import com.nguyenhuugiap.computer_shop.enums.VariantStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProductVariantUpdateStatusRequest {
    VariantStatus status;
}
