package com.nguyenhuugiap.computer_shop.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nguyenhuugiap.computer_shop.enums.CategoryStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategorySlimResponse {
    Long id;
    String name;
    String description;
    CategoryStatus status;
    String slug;
}

