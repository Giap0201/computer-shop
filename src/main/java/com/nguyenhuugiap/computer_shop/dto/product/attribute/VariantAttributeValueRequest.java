package com.nguyenhuugiap.computer_shop.dto.product.attribute;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VariantAttributeValueRequest {
    @NotNull(message = "ATTRIBUTE_ID_REQUIRED")
    Long attributeId;
    @NotBlank(message = "VALUE_ATTRIBUTE_REQUIRED")
    String value;
}
