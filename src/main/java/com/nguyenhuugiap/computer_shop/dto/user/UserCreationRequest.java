package com.nguyenhuugiap.computer_shop.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Email(message = "EMAIL_INVALID")
    @NotBlank(message = "EMAIL_INVALID")
    String email;
    @NotBlank(message = "PASSWORD_INVALID")
    @Size(min = 8, message = "PASSWORD_INVALID")
    String password;
    @NotBlank(message = "PHONE_INVALID")
    @Pattern(regexp = "^(0[0-9]{9})$", message = "PHONE_INVALID")
    String phone;
    @NotBlank(message = "USERNAME_INVALID")
    @Size(min = 2, max = 50, message = "USERNAME_INVALID")
    String fullName;
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "GENDER_INVALID")
    String gender;
}
