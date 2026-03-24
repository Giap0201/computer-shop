package com.nguyenhuugiap.computer_shop.dto.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {

    Long id;

    List<CartItemResponse> items;

    long totalItems;
    BigDecimal totalPrice;
    Long userId;
    String sessionId;
}