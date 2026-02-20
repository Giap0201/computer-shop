package com.nguyenhuugiap.computer_shop.dto.request.category;

import com.nguyenhuugiap.computer_shop.enums.CategoryStatus;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryUpdateRequest {
    Long parentId;
    @Size(max = 100, message = "CATEGORY_TOO_LONG")
    String name;
    String description;
    CategoryStatus status;
}
