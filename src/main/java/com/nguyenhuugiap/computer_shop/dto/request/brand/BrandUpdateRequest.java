package com.nguyenhuugiap.computer_shop.dto.request.brand;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BrandUpdateRequest {
    @Size(max = 100, message = "BRAND_NAME_INVALID")
    String name;
    String logo;
}
