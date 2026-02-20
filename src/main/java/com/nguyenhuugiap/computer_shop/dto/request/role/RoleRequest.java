package com.nguyenhuugiap.computer_shop.dto.request.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor

public class RoleRequest {
    @NotBlank(message = "ROLE_NAME_REQUIRED")
    @Size(max = 50, message = "ROLE_TOO_LONG")
    String name;
    String description;
}
