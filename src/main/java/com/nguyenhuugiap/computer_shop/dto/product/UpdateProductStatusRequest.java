package com.nguyenhuugiap.computer_shop.dto.product;


import com.nguyenhuugiap.computer_shop.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProductStatusRequest {
    ProductStatus productStatus;
}
