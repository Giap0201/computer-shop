package com.nguyenhuugiap.computer_shop.dto.product.image;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ProductImageResponse {
    Long id;
    String imageUrl;
    int displayOrder;
    Long productId;
}
