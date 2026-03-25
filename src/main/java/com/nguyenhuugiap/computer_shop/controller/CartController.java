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

import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;
    CookieUtils cookieUtils;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(@RequestBody @Valid CartItemCreationRequest request,
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
    public ApiResponse<CartResponse> getMyCart(@CookieValue(value = CookieUtils.CART_SESSION_COOKIE_NAME, required = false)
                                               String sessionId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart(sessionId))
                .build();
    }

    @PatchMapping("/items/{productVariantId}/quantity")
    public ResponseEntity<ApiResponse<CartResponse>> updateItemQuantity(@PathVariable Long productVariantId,
                                                                        @RequestParam(name = "value") long newQuantity,
                                                                        @CookieValue(value = CookieUtils.CART_SESSION_COOKIE_NAME, required = false) String sessionId) {
        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .result(cartService.updateItemQuantity(sessionId, productVariantId, newQuantity))
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/items/{productVariantId}")
    public ResponseEntity<ApiResponse<CartResponse>> deleteItem(@PathVariable Long productVariantId,
                                                                @CookieValue(value = CookieUtils.CART_SESSION_COOKIE_NAME, required = false) String sessionId) {
        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .result(cartService.removeItem(sessionId, productVariantId))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(@CookieValue(value = CookieUtils.CART_SESSION_COOKIE_NAME, required = false) String sessionId) {

        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .result(cartService.clearCart(sessionId))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> deleteItems(@RequestParam(name = "ids") List<Long> productVariantIds,
                                                                 @CookieValue(value = CookieUtils.CART_SESSION_COOKIE_NAME, required = false) String sessionId) {
        ApiResponse<CartResponse> response = ApiResponse.<CartResponse>builder()
                .result(cartService.removeItems(sessionId, productVariantIds)).build();
        return ResponseEntity.ok(response);
    }
}
