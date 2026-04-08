package com.nguyenhuugiap.computer_shop.dto.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    Long id;
    Long productId;
    Long variantId;

    String productName;
    String skuCode;
    String variantAttributes;

    Integer quantity;
    BigDecimal priceAtPurchase;
    BigDecimal subTotal;
}
