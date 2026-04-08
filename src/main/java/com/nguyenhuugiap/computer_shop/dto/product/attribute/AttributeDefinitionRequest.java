package com.nguyenhuugiap.computer_shop.dto.product.attribute;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributeDefinitionRequest {
    @NotBlank(message = "NAME_ATTRIBUTE_REQUIRED")
    String name;
}
