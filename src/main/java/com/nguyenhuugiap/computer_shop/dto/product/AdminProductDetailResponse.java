package com.nguyenhuugiap.computer_shop.dto.product;

import com.nguyenhuugiap.computer_shop.dto.product.image.ImageSlimResponse;
import com.nguyenhuugiap.computer_shop.dto.product.product_variant.VariantSlimResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class AdminProductDetailResponse {
    ProductResponse product;
    List<VariantSlimResponse> productVariantResponse;
    List<ImageSlimResponse> productImageResponse;
}
