package com.nguyenhuugiap.computer_shop.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleAssignmentRequest {
    @NotBlank(message = "ROLE_NAME_INVALID")
    String roleName;
}