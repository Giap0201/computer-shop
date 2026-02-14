package com.nguyenhuugiap.computer_shop.dto.response;

import com.nguyenhuugiap.computer_shop.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long id;
    String name;
    String description;
    CategoryResponse category;
    BrandResponse brand;
    String slug;
    ProductStatus status;
    BigDecimal price;
    String thumbnail;
    String code;
}
