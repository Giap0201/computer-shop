package com.nguyenhuugiap.computer_shop.dto.request.authenticate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LogoutRequest {
    String token;
    String refreshToken;
}
