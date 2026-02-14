package com.nguyenhuugiap.computer_shop.dto.request;

import com.nguyenhuugiap.computer_shop.enums.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được bỏ trống")
    @Size(max = 200, message = "Tên sản phẩm phải bé hơn 200 kí tự")
    String name;
    @Size(max = 50, message = "Code phải nhỏ hơn 50 kí tự")
    String code;
    @NotBlank(message = "Ảnh không được bỏ trống")
    String thumbnail;
    @NotBlank(message = "Mô tả không được bỏ trống")
    String description;
    @NotNull(message = "Trạng thái không được bỏ trống")
    ProductStatus status;
    @NotNull(message = "Vui lòng thêm danh mục sản phẩm")
    Long categoryId;
    @NotNull(message = "Vui lòng thêm thương hiệu sản phẩm")
    Long brandId;
    @NotNull(message = "Giá không được bỏ trống")
    @Min(value = 0, message = "Giá phải lớn hơn 0")
    BigDecimal price;
}
