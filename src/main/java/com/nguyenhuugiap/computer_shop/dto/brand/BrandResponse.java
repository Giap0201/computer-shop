package com.nguyenhuugiap.computer_shop.dto.brand;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BrandResponse {
    Long id;
    String name;
    String logoUrl;
    String slug;
}
