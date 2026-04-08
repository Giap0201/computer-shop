package com.nguyenhuugiap.computer_shop.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nguyenhuugiap.computer_shop.enums.Gender;
import com.nguyenhuugiap.computer_shop.enums.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    Long id;
    String fullName;
    String phone;
    String email;
    Gender gender;
    UserStatus status;
    LocalDateTime createdAt;
}
