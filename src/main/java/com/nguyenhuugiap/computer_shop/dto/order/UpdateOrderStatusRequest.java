package com.nguyenhuugiap.computer_shop.dto.order;

import com.nguyenhuugiap.computer_shop.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateOrderStatusRequest {
    OrderStatus status;
    String note;
}
