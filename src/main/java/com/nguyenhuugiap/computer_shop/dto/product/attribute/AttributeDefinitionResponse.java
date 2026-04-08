package com.nguyenhuugiap.computer_shop.dto.product.attribute;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributeDefinitionResponse {
    String name;
    Long id;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
