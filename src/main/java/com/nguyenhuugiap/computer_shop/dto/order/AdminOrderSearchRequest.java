package com.nguyenhuugiap.computer_shop.dto.order;

import com.nguyenhuugiap.computer_shop.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminOrderSearchRequest {
    String orderCode;
    OrderStatus status;
    LocalDateTime fromDate;
    LocalDateTime toDate;
}
