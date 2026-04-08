package com.nguyenhuugiap.computer_shop.dto.order;

import com.nguyenhuugiap.computer_shop.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderCreationRequest {
    @NotBlank(message = "SHIPPING_NAME_REQUIRED")
    @Size(max = 100, message = "INVALID_SHIPPING_NAME")
    String shippingName;

    @NotBlank(message = "SHIPPING_PHONE_REQUIRED")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "INVALID_SHIPPING_PHONE")
    String shippingPhone;

    @NotBlank(message = "SHIPPING_ADDRESS_REQUIRED")
    String shippingAddress;

    String note;

    boolean fromCart;

    @NotNull(message = "PAYMENT_METHOD_REQUIRED")
    PaymentMethod paymentMethod = PaymentMethod.VNPAY;

    @NotEmpty(message = "ITEMS_REQUIRED")
    @Valid
    List<OrderItemRequest> items;
}
