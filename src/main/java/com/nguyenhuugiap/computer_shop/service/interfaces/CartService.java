package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.cart.CartItemCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.cart.CartResponse;

public interface CartService {
    CartResponse addToCart(CartItemCreationRequest request, String sessionId);
    CartResponse getCart(String sessionId);
    void mergeCart(String sessionId, Long userId);
    CartResponse updateItemQuantity(String sessionId, Long productVariantId, long newQuantity);
    CartResponse removeItem(String sessionId, Long productVariantId);
}
