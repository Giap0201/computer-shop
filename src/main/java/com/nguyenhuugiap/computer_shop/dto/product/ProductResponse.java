package com.nguyenhuugiap.computer_shop.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nguyenhuugiap.computer_shop.dto.brand.BrandResponse;
import com.nguyenhuugiap.computer_shop.dto.category.CategoryResponse;
import com.nguyenhuugiap.computer_shop.enums.ProductStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    Long id;

    String name;

    String description;

    CategoryResponse category;

    BrandResponse brand;

    String slug;

    ProductStatus status;

    BigDecimal minPrice;

    String thumbnailUrl;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

}
