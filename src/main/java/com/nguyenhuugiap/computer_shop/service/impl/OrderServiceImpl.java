package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.order.OrderCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.order.OrderItemRequest;
import com.nguyenhuugiap.computer_shop.dto.order.OrderResponse;
import com.nguyenhuugiap.computer_shop.entity.*;
import com.nguyenhuugiap.computer_shop.enums.OrderStatus;
import com.nguyenhuugiap.computer_shop.enums.PaymentStatus;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.mapper.OrderMapper;
import com.nguyenhuugiap.computer_shop.repository.OrderRepository;
import com.nguyenhuugiap.computer_shop.repository.ProductVariantRepository;
import com.nguyenhuugiap.computer_shop.repository.UserRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.CartService;
import com.nguyenhuugiap.computer_shop.service.interfaces.OrderService;
import com.nguyenhuugiap.computer_shop.utils.OrderUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    UserRepository userRepository;
    ProductVariantRepository productVariantRepository;
    CartService cartService;
    final BigDecimal SHIPPING_FEE = new BigDecimal(10000);

    @Transactional
    @Override
    public OrderResponse createOrder(OrderCreationRequest request) {
        User user = getUser();
        String orderCode = handlingOrderCode();

        // Validate data cart
        if (request.isFromCart()) {
            handlingCartItem(request.getItems());
        }
        List<OrderItem> items = new ArrayList<>();
        if (request.getItems() != null) {
            items = handlingOrderItems(request.getItems());
        }

        BigDecimal totalAmount = items.stream().map(item -> item.getPriceAtPurchase()
                .multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalAmount = totalAmount.add(SHIPPING_FEE);
        Order order = Order.builder()
                .orderCode(orderCode)
                .totalAmount(totalAmount)
                .shippingFee(SHIPPING_FEE)
                .finalAmount(finalAmount)
                .shippingName(request.getShippingName())
                .shippingPhone(request.getShippingPhone())
                .shippingAddress(request.getShippingAddress())
                .note(request.getNote())
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.UNPAID)
                .user(user)
                .build();
        for (OrderItem item : items) order.addOrderItem(item);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .status(OrderStatus.PENDING)
                .note("Khách hàng đặt đơn mới")
                .build();
        order.addStatusHistory(history);

        if (request.isFromCart()) {
            List<Long> variantIds = request.getItems().stream()
                    .map(OrderItemRequest::getVariantId).toList();
            cartService.removeItems(null, variantIds);
        }

        return orderMapper.toResponse(orderRepository.save(order));
    }

    private User getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAuthenticated = username != null && !username.equals("anonymousUser");
        if (!isAuthenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);
        User user = null;
        try {
            Long userId = Long.valueOf(username);
            user = userRepository.getReferenceById(userId);
        } catch (NumberFormatException e) {
            log.error("NumberFormatException", e);
        }
        if (user == null) throw new AppException(ErrorCode.USER_NOT_FOUND);
        return user;
    }

    private String handlingOrderCode() {
        String orderCode;
        int counter = 0;
        do {
            orderCode = OrderUtils.generateOrderCode();
            counter++;
            if (!orderRepository.existsByOrderCode(orderCode)) return orderCode;
            if (counter > 5) throw new AppException(ErrorCode.CANNOT_CREATE_ORDER_CODE);
        } while (true);
    }

    // Map order items request to order item entities
    private List<OrderItem> handlingOrderItems(List<OrderItemRequest> requests) {
        List<OrderItem> orderItems = new ArrayList<>();
        List<Long> variantIds = requests.stream().map(OrderItemRequest::getVariantId).toList();

        // User JOIN FETCH to eagerly load product variants and avoid N+1 query problem
        List<ProductVariant> productVariants = productVariantRepository.findVariantsWithProductByIds(variantIds);

        Map<Long, ProductVariant> variantMap = productVariants.stream()
                .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        for (OrderItemRequest request : requests) {
            ProductVariant variant = variantMap.get(request.getVariantId());
            if (variant == null) throw new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND);

            Long updated = productVariantRepository.deductStock(variant.getId(), Long.valueOf(request.getQuantity()));
            if (updated == 0) throw new AppException(ErrorCode.INSUFFICIENT_STOCK);

            // Convert attribute values to a readable string for storing in order items
            String variantAttributes = "";
            if (variant.getAttributeValues() != null && !variant.getAttributeValues().isEmpty()) {
                variantAttributes = variant.getAttributeValues().stream()
                        .map(attVal -> attVal.getAttributeDefinition().getName() + ": " + attVal.getValue())
                        .collect(Collectors.joining(", "));
            }
            OrderItem orderItem = OrderItem.builder()
                    .productName(variant.getProduct().getName())
                    .skuCode(variant.getSkuCode())
                    .quantity(request.getQuantity())
                    .priceAtPurchase(variant.getPrice())
                    .product(variant.getProduct())
                    .productVariant(variant)
                    .variantAttributes(variantAttributes)
                    .build();
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private void handlingCartItem(List<OrderItemRequest> requests) {
        Cart currentCart = cartService.getCartEntity();
        if (currentCart == null || currentCart.getCartItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_IS_EMPTY);
        }

        // Tạo Map: Key = variantId, Value = quantity đang có trong giỏ
        Map<Long, Integer> cartItemMap = currentCart.getCartItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getProductVariant().getId(),
                        item -> (Math.toIntExact(item.getQuantity()))
                ));

        for (OrderItemRequest reqItem : requests) {
            Integer quantityInCart = cartItemMap.get(reqItem.getVariantId());
            if (quantityInCart == null) {
                throw new AppException(ErrorCode.INVALID_CART_DATA);
            }
            if (!quantityInCart.equals(reqItem.getQuantity())) {
                throw new AppException(ErrorCode.INVALID_CART_DATA);
            }
        }
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        return null;
    }

    @Override
    public OrderResponse getOrderByCode(String orderCode) {
        return null;
    }

    @Override
    public Page<OrderResponse> getMyOrders(Pageable pageable) {
        return null;
    }

    @Override
    public void cancelOrder(Long orderId, String reason) {

    }

    @Override
    public void updateStatus(Long orderId, OrderStatus newStatus, String note) {

    }

    @Override
    public void handlePaymentCallback(String orderCode, boolean isSuccess) {

    }
}
