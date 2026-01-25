package com.nguyenhuugiap.computer_shop.dto.response;

import com.nguyenhuugiap.computer_shop.enums.CategoryStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    Long id;
    String name;
    String description;
    CategoryStatus status;
    String slug;
    Set<CategoryResponse> children;
    Long parentId;
    String parentName;
}
