package com.nguyenhuugiap.computer_shop.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationRequest {
    @NotBlank(message = "PRODUCT_NAME_REQUIRED")
    @Size(min = 3, max = 255, message = "PRODUCT_NAME_INVALID")
    String name;

    @Size(max = 400, message = "DESCRIPTION_TOO_LONG")
    String description;

    String thumbnailUrl;

    @NotNull(message = "CATEGORY_REQUIRED")
    Long categoryId;

    @NotNull(message = "BRAND_REQUIRED")
    Long brandId;

}
