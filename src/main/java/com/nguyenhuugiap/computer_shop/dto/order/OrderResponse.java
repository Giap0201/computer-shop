package com.nguyenhuugiap.computer_shop.dto.order;

import com.nguyenhuugiap.computer_shop.enums.OrderStatus;
import com.nguyenhuugiap.computer_shop.enums.PaymentMethod;
import com.nguyenhuugiap.computer_shop.enums.PaymentStatus;
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
public class OrderResponse {
    Long id;
    String orderCode;

    BigDecimal totalAmount;
    BigDecimal shippingFee;
    BigDecimal finalAmount;

    String shippingName;
    String shippingPhone;
    String shippingAddress;
    String note;

    OrderStatus status;
    PaymentMethod paymentMethod;
    PaymentStatus paymentStatus;

    List<OrderItemResponse> items;

    LocalDateTime createdAt;
}
