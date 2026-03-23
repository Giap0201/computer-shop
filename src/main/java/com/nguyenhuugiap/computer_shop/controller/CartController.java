package com.nguyenhuugiap.computer_shop.controller;


import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.cart.CartItemCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.cart.CartResponse;
import com.nguyenhuugiap.computer_shop.security.CookieUtils;
import com.nguyenhuugiap.computer_shop.service.interfaces.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;
    CookieUtils cookieUtils;

    @PostMapping("/item")
    ResponseEntity<ApiResponse<CartResponse>> addItemToCart(@RequestBody @Valid CartItemCreationRequest request,
                                                            @CookieValue(value = CookieUtils.CART_SESSION_COOKIE_NAME, required = false) String sessionId) {
        CartResponse cartResponse = cartService.addToCart(request, sessionId);

        ResponseCookie cookie = cookieUtils.createCartSessionCookie(cartResponse.getSessionId());
        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .result(cartResponse)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);

    }

    @GetMapping("/my-cart")
    ApiResponse<CartResponse> getMyCart(@CookieValue(value = CookieUtils.CART_SESSION_COOKIE_NAME, required = false)
                                        String sessionId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart(sessionId))
                .build();
    }

}
