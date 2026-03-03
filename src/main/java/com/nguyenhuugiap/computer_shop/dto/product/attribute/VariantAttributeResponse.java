package com.nguyenhuugiap.computer_shop.dto.product.attribute;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VariantAttributeResponse {
    Long id;
    Long attributeId;
    String attributeName;
    String value;
}
