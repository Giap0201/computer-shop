package com.nguyenhuugiap.computer_shop.dto.request.category;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreationRequest {
    Long parentId;
    @NotBlank(message = "CATEGORY_NAME_REQUIRED")
    @Size(max = 100, message = "CATEGORY_TOO_LONG")
    String name;
    String description;
}
