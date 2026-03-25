package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.cart.CartItemCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.cart.CartItemResponse;
import com.nguyenhuugiap.computer_shop.dto.cart.CartResponse;
import com.nguyenhuugiap.computer_shop.entity.Cart;
import com.nguyenhuugiap.computer_shop.entity.CartItem;
import com.nguyenhuugiap.computer_shop.entity.ProductVariant;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
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
import java.util.*;

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
        long quantityToAdd = request.getQuantity();

        // check if stock is valid
        ProductVariant productVariant = productVariantService.getEntityProductVariant(variantId);
        if (quantityToAdd > productVariant.getStockQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }
        int updatedRows = cartItemRepository.addQuantityToExistingItem(cart.getId(), variantId, quantityToAdd);
        if (updatedRows == 0) {
            ProductVariant variantProxy = productVariantService.getEntityProductVariant(variantId);
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(variantProxy)
                    .quantity(quantityToAdd)
                    .build();
            try {
                cartItemRepository.save(newItem);
            } catch (DataIntegrityViolationException e) {
                log.error("Conflict khi lưu CartItem", e);
//                throw new RuntimeException("Lỗi đồng bộ dữ liệu giỏ hàng");
            }
        }
        return buildCartResponse(cart);
    }

    @Override
    public CartResponse getCart(String sessionId) {
        Cart cart = checkCart(sessionId);
        if (cart == null) {
            return CartResponse.builder()
                    .items(List.of())
                    .totalItems(0)
                    .totalPrice(BigDecimal.ZERO)
                    .sessionId(sessionId)
                    .build();
        }
        return buildCartResponse(cart);
    }

    @Transactional
    @Override
    public void mergeCart(String sessionId, Long userId) {
        if (sessionId == null || sessionId.isBlank()) return;

        Cart guestCart = cartRepository.findBySessionId(sessionId).orElse(null);
        if (guestCart == null) return;

        List<CartItem> guestItems = cartItemRepository.findAllByCart_Id(guestCart.getId());
        if (guestItems.isEmpty()) {
            cartRepository.delete(guestCart);
            return;
        }

        Cart userCart = cartRepository.findByUser_Id(userId).orElseGet(() -> {
            Cart newCart = Cart.builder()
                    .user(userRepository.getReferenceById(userId))
                    .build();
            return cartRepository.save(newCart);
        });

        List<CartItem> userItems = cartItemRepository.findAllByCart_Id(userCart.getId());
        Map<Long, CartItem> userItemMap = new HashMap<>();
        for (CartItem item : userItems) {
            userItemMap.put(item.getProductVariant().getId(), item);
        }

        List<CartItem> itemsToDelete = new ArrayList<>();

        for (CartItem guestItem : guestItems) {
            Long variantId = guestItem.getProductVariant().getId();
            long currentStock = guestItem.getProductVariant().getStockQuantity();
            if (userItemMap.containsKey(variantId)) {
                CartItem existingUserItem = userItemMap.get(variantId);
                long combinedQuantity = existingUserItem.getQuantity() + guestItem.getQuantity();

                // check if stock is valid
                if (combinedQuantity > currentStock) combinedQuantity = currentStock;
                existingUserItem.setQuantity(combinedQuantity);
                itemsToDelete.add(guestItem);
            } else {
                userCart.addItem(guestItem);
                cartItemRepository.save(guestItem);
            }
        }

        if (!itemsToDelete.isEmpty()) {
            cartItemRepository.deleteAll(itemsToDelete);
        }
        cartRepository.delete(guestCart);
    }

    @Transactional
    @Override
    public CartResponse updateItemQuantity(String sessionId, Long productVariantId, long newQuantity) {
        if (newQuantity < 1) {
            return removeItem(sessionId, productVariantId);
        }
        Cart cart = checkCart(sessionId);
        if (cart == null) {
            return CartResponse.builder()
                    .items(List.of())
                    .totalItems(0)
                    .totalPrice(BigDecimal.ZERO)
                    .sessionId(sessionId)
                    .build();
        }

        List<CartItem> cartItems = cart.getCartItems();
        boolean checkUpdate = false;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductVariant().getId().equals(productVariantId)) {
                long stock = cartItem.getProductVariant().getStockQuantity();
                cartItem.setQuantity(Math.min(newQuantity, stock));
                checkUpdate = true;
                break;
            }
        }
        if (!checkUpdate) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND_IN_CART);
        return buildCartResponse(cart);
    }

    @Transactional
    @Override
    public CartResponse removeItem(String sessionId, Long productVariantId) {
        Cart cart = checkCart(sessionId);
        if (cart == null) {
            return CartResponse.builder()
                    .items(List.of())
                    .totalItems(0)
                    .totalPrice(BigDecimal.ZERO)
                    .sessionId(sessionId)
                    .build();
        }

        List<CartItem> cartItems = cart.getCartItems();
        CartItem removeItem = null;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductVariant().getId().equals(productVariantId)) {
                removeItem = cartItem;
                break;
            }
        }
        if (removeItem == null) throw new AppException(ErrorCode.PRODUCT_NOT_FOUND_IN_CART);
        cartItems.remove(removeItem);
        cartItemRepository.delete(removeItem);
        return buildCartResponse(cart);
    }

    @Transactional
    @Override
    public CartResponse removeItems(String sessionId, List<Long> listProductVariantId) {
        Cart cart = checkCart(sessionId);
        if (cart == null) {
            return CartResponse.builder()
                    .items(List.of())
                    .totalItems(0)
                    .totalPrice(BigDecimal.ZERO)
                    .sessionId(sessionId)
                    .build();
        }

        List<CartItem> cartItems = cart.getCartItems();
        List<CartItem> itemsToDelete = new ArrayList<>();

        for (CartItem item : cartItems) {
            if (listProductVariantId.contains(item.getProductVariant().getId())) {
                itemsToDelete.add(item);
            }
        }
        if (!itemsToDelete.isEmpty()) {
            cartItems.removeAll(itemsToDelete);
            cartItemRepository.deleteAll(itemsToDelete);
        }
        return buildCartResponse(cart);
    }

    @Transactional
    @Override
    public CartResponse clearCart(String sessionId) {
        Cart cart = checkCart(sessionId);
        if (cart != null) {
            cartItemRepository.deleteAllByCart_Id(cart.getId());
            if (!cart.getCartItems().isEmpty()) {
                cart.getCartItems().clear();
            }
        }
        return CartResponse.builder()
                .items(List.of())
                .totalItems(0)
                .totalPrice(BigDecimal.ZERO)
                .sessionId(sessionId)
                .build();

    }


    private Cart checkCart(String sessionId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAuthenticated = username != null && !username.equals("anonymousUser");
        Cart cart = null;
        if (isAuthenticated) {
            try {
                Long userId = Long.valueOf(username);
                cart = cartRepository.findByUser_Id(userId).orElse(null);
            } catch (NumberFormatException e) {
                log.error("NumberFormatException", e);
            }
        } else if (sessionId != null && !sessionId.isBlank()) {
            cart = cartRepository.findBySessionId(sessionId).orElse(null);
        }
        return cart;
    }


    private CartResponse buildCartResponse(Cart cart) {
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

        long totalItemsCount = items.stream()
                .mapToLong(CartItemResponse::getQuantity)
                .sum();

        String responseSessionId = cart.getSessionId();
        Long userId = cart.getUser() == null ? null : cart.getUser().getId();

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalItems(totalItemsCount)
                .totalPrice(total)
                .sessionId(responseSessionId)
                .userId(userId)
                .build();
    }
}