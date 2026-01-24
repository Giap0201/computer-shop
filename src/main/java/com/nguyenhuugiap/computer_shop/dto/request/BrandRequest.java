package com.nguyenhuugiap.computer_shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BrandRequest {
    @NotBlank(message = "Tên thương hiệu không được bỏ trống")
    @Size(max = 100, message = "Tên thương hiệu nhỏ hơn 100 kí tự")
    String name;
    String logoUrl;
}
