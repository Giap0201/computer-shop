package com.nguyenhuugiap.computer_shop.dto.request;

import com.nguyenhuugiap.computer_shop.enums.Gender;
import com.nguyenhuugiap.computer_shop.enums.UserStatus;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min = 2, max = 50, message = "USERNAME_INVALID")
    String fullName;
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
    Gender gender;
    UserStatus status;
}
