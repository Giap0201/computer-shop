package com.nguyenhuugiap.computer_shop.dto.product.image;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImageUpdateRequest {
    Long id;

    @NotBlank(message = "IMAGE_REQUIRED")
    String imageUrl;

    int displayOrder;
}