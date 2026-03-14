package com.nguyenhuugiap.computer_shop.dto.product.attribute;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class VariantAttributeResponse {
    Long id;
    Long attributeId;
    String attributeName;
    String value;
}
