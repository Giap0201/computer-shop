package com.nguyenhuugiap.computer_shop.dto.product;

import com.nguyenhuugiap.computer_shop.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSearchRequest {
    String keyword;
    Long categoryId;
    Long brandId;
    ProductStatus status;
    BigDecimal fromPrice;
    BigDecimal toPrice;
}
