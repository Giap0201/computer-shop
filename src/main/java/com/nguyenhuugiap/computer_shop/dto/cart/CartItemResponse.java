package com.nguyenhuugiap.computer_shop.dto.cart;

import com.nguyenhuugiap.computer_shop.dto.product.attribute.VariantAttributeResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long id;
    Long productVariantId;
    String productName;
    String attributes;
    String imageUrl;
    long quantity;
    BigDecimal unitPrice;
    BigDecimal subTotal;
}