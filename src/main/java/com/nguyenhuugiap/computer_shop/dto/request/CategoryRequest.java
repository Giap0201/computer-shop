package com.nguyenhuugiap.computer_shop.dto.request;


import com.nguyenhuugiap.computer_shop.enums.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {
    Long parentId;
    @NotBlank(message = "Tên không được bỏ trống")
    @Size(max = 100, message = "Tên danh mục phải nhỏ hơn 100 kí tự")
    String name;
    @NotNull(message = "Trạng thái không được bỏ trống")
    CategoryStatus status;
}
