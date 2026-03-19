package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.cart.CartItemCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.cart.CartItemResponse;
import com.nguyenhuugiap.computer_shop.dto.cart.CartResponse;
import com.nguyenhuugiap.computer_shop.entity.Cart;
import com.nguyenhuugiap.computer_shop.entity.CartItem;
import com.nguyenhuugiap.computer_shop.entity.ProductVariant;
import com.nguyenhuugiap.computer_shop.mapper.CartItemMapper;
import com.nguyenhuugiap.computer_shop.repository.CartItemRepository;
import com.nguyenhuugiap.computer_shop.repository.CartRepository;
import com.nguyenhuugiap.computer_shop.repository.UserRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.CartService;
import com.nguyenhuugiap.computer_shop.service.interfaces.ProductVariantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartServiceImpl implements CartService {

    CartItemRepository cartItemRepository;
    CartRepository cartRepository;
    UserRepository userRepository;
    CartItemMapper cartItemMapper;
    ProductVariantService productVariantService;

    @Override
    @Transactional
    public CartResponse addToCart(CartItemCreationRequest request, String sessionId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAuthenticated = username != null && !username.equals("anonymousUser");

        Cart cart;

        if (isAuthenticated) {
            Long userId = Long.valueOf(username);
            cart = cartRepository.findByUser_Id(userId).orElseGet(() -> {
                Cart newCart = Cart.builder()
                        .user(userRepository.getReferenceById(userId))
                        .build();
                return cartRepository.save(newCart);
            });
        } else {
            if (sessionId == null || sessionId.isBlank()) {
                String newSessionId = UUID.randomUUID().toString();
                Cart newCart = Cart.builder()
                        .sessionId(newSessionId)
                        .build();
                cart = cartRepository.save(newCart);
            } else {
                final String currentSessionId = sessionId;
                cart = cartRepository.findBySessionId(currentSessionId).orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .sessionId(currentSessionId)
                            .build();
                    return cartRepository.save(newCart);
                });
            }
        }
        Long variantId = request.getProductVariantId();
        int quantityToAdd = request.getQuantity();
        ProductVariant variantProxy = productVariantService.getEntityProductVariant(variantId);
        int updatedRows = cartItemRepository.addQuantityToExistingItem(cart.getId(), variantId, quantityToAdd);
        if (updatedRows == 0) {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(variantProxy)
                    .quantity(quantityToAdd)
                    .build();
            try {
                cartItemRepository.save(newItem);
            } catch (DataIntegrityViolationException e) {
            }
        }

        List<CartItem> currentItems = cartItemRepository.findAllByCart_Id(cart.getId());

        List<CartItemResponse> items = currentItems.stream()
                .map(cartItem -> {
                    CartItemResponse response = cartItemMapper.toCartItemResponse(cartItem);
                    BigDecimal price = response.getUnitPrice() != null
                            ? response.getUnitPrice()
                            : BigDecimal.ZERO;
                    BigDecimal quantity = BigDecimal.valueOf(cartItem.getQuantity());
                    response.setSubTotal(price.multiply(quantity));
                    return response;
                })
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItemsCount = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        String responseSessionId = cart.getSessionId();

        Long responseUserId = isAuthenticated ? Long.valueOf(username) : null;

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalItems(totalItemsCount)
                .totalPrice(total)
                .sessionId(responseSessionId)
                .userId(responseUserId)
                .build();
    }

}