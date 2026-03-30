package com.nguyenhuugiap.computer_shop.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateShippingRequest {
    @Size(max = 100, message = "INVALID_SHIPPING_NAME")
    String shippingName;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "INVALID_SHIPPING_PHONE")
    String shippingPhone;

    String shippingAddress;

    String note;
}
