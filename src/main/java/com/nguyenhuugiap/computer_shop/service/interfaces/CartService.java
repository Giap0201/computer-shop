package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.cart.CartItemCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.cart.CartResponse;

public interface CartService {
    CartResponse addToCart(CartItemCreationRequest request, String sessionId);
}
