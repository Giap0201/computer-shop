package com.nguyenhuugiap.computer_shop.security;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {
    public static final String CART_SESSION_COOKIE_NAME = "CART_SESSION_ID";

    public ResponseCookie createCartSessionCookie(String sessionId) {
        return ResponseCookie.from(CART_SESSION_COOKIE_NAME, sessionId)
                .httpOnly(true)         // Chống XSS (JS không đọc được)
                .secure(true)           // Chống MitM (Chỉ gửi qua HTTPS/Localhost)
                .sameSite("Lax")        // Chống CSRF (Cho phép điều hướng an toàn)
                .path("/")              // Cookie có hiệu lực trên toàn bộ API
                .maxAge(7 * 24 * 60 * 60) // Thời gian sống: 7 ngày (Tính bằng giây)
                .build();
    }

    public ResponseCookie deleteCartSessionCookie() {
        // Ghi đè cookie cũ bằng một cookie hết hạn ngay lập tức
        return ResponseCookie.from(CART_SESSION_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
    }
}
