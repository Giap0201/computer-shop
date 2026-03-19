package com.nguyenhuugiap.computer_shop.controller;


import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.cart.CartItemCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.cart.CartResponse;
import com.nguyenhuugiap.computer_shop.service.interfaces.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @PostMapping("/item")
    ApiResponse<CartResponse> addItemToCart(
            @Valid @RequestBody CartItemCreationRequest request,
            @RequestHeader(value = "X-Cart-Session-Id", required = false) String sessionId) {
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addToCart(request, sessionId))
                .build();
    }

}
