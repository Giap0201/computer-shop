package com.nguyenhuugiap.computer_shop.dto.authenticate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticateResponse {
    String token;
    String refreshToken;
    boolean authenticated;
    Long userId;
}
