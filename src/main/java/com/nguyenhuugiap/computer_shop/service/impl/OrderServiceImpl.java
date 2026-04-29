package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.PageResponse;
import com.nguyenhuugiap.computer_shop.dto.order.*;
import com.nguyenhuugiap.computer_shop.entity.*;
import com.nguyenhuugiap.computer_shop.enums.OrderStatus;
import com.nguyenhuugiap.computer_shop.enums.PaymentMethod;
import com.nguyenhuugiap.computer_shop.enums.PaymentStatus;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.mapper.OrderMapper;
import com.nguyenhuugiap.computer_shop.repository.OrderRepository;
import com.nguyenhuugiap.computer_shop.repository.ProductVariantRepository;
import com.nguyenhuugiap.computer_shop.repository.UserRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.CartService;
import com.nguyenhuugiap.computer_shop.service.interfaces.OrderService;
import com.nguyenhuugiap.computer_shop.specification.OrderSpecification;
import com.nguyenhuugiap.computer_shop.utils.OrderUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        // Get current authenticated user
        User user = getUser();

        // Generate unique order code
        String orderCode = handlingOrderCode();

        // Validate cart data if order is created from cart
        if (request.isFromCart()) {
            handlingCartItem(request.getItems());
        }

        // Convert request items
        List<OrderItem> items = new ArrayList<>();
        if (request.getItems() != null) {
            items = handlingOrderItems(request.getItems());
        }

        // Calculate total amount
        BigDecimal totalAmount = items.stream().map(item -> item.getPriceAtPurchase()
                .multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalAmount = totalAmount.add(SHIPPING_FEE);

        // Build order entity
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

        // Add items to order
        for (OrderItem item : items) order.addOrderItem(item);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .status(OrderStatus.PENDING)
                .note("Khách hàng đặt đơn mới")
                .build();
        order.addStatusHistory(history);

        // Remove item from cart after successful order creation
        if (request.isFromCart()) {
            List<Long> variantIds = request.getItems().stream()
                    .map(OrderItemRequest::getVariantId).toList();
            cartService.removeItems(null, variantIds);
        }

        // Save order
        return orderMapper.toResponse(orderRepository.save(order));
    }

    private User getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAuthenticated = username != null && !username.equals("anonymousUser");
        if (!isAuthenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);
        User user = null;
        try {
            Long userId = Long.valueOf(username);
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        } catch (NumberFormatException e) {
            log.error("NumberFormatException", e);
        }
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

        // Extract all variantIds
        List<Long> variantIds = requests.stream().map(OrderItemRequest::getVariantId).toList();

        // Fetch all variants with product using JOIN FETCH (avoid N+1 problem)
        List<ProductVariant> productVariants = productVariantRepository.findVariantsWithProductByIds(variantIds);

        // Map for quick lookup
        Map<Long, ProductVariant> variantMap = productVariants.stream()
                .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        for (OrderItemRequest request : requests) {
            ProductVariant variant = variantMap.get(request.getVariantId());
            if (variant == null) throw new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND);

            // Deduct stock using atomic update query (avoid race condition)
            Long updated = productVariantRepository.deductStock(variant.getId(), Long.valueOf(request.getQuantity()));
            if (updated == 0) throw new AppException(ErrorCode.INSUFFICIENT_STOCK);

            // Convert attribute values to a readable string for storing in order items
            String variantAttributes = "";
            if (variant.getAttributeValues() != null && !variant.getAttributeValues().isEmpty()) {
                variantAttributes = variant.getAttributeValues().stream()
                        .map(attVal -> attVal.getAttributeDefinition().getName() + ": " + attVal.getValue())
                        .collect(Collectors.joining(", "));
            }

            // Create order item snapshot
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
            // Validate item exists to cart
            if (quantityInCart == null) {
                throw new AppException(ErrorCode.INVALID_CART_DATA);
            }

            // Ensure FE data is not tampered (important security validation)
            if (!quantityInCart.equals(reqItem.getQuantity())) {
                throw new AppException(ErrorCode.INVALID_CART_DATA);
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = getOrderEntity(orderId);
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getOrderByCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode).orElseThrow(() ->
                new AppException(ErrorCode.ORDER_NOT_FOUND));
        return orderMapper.toResponse(order);
    }

    @Override
    public PageResponse<OrderResponse> getMyOrders(Pageable pageable) {
        return null;
    }

    @Override
    public Order getOrderEntityByCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode).orElseThrow(() ->
                new AppException(ErrorCode.ORDER_NOT_FOUND));
    }


    @Override
    public PageResponse<OrderResponse> getAllOrderAsAdmin(AdminOrderSearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Specification<Order> spec = OrderSpecification.getSearchSpec(request);

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        List<OrderResponse> content = orderPage.getContent().stream()
                .map(orderMapper::toResponse)
                .toList();

        return PageResponse.<OrderResponse>builder()
                .currentPage(page)
                .pageSize(orderPage.getSize())
                .totalPages(orderPage.getTotalPages())
                .totalElements(orderPage.getTotalElements())
                .data(content)
                .build();
    }


    @Transactional
    @Override
    public void cancelOrderAsUser(Long orderId, CancelOrderRequest request) {
        Order order = getOrderEntity(orderId);

        User currentUser = getUser();
        if (!currentUser.getId().equals(order.getUser().getId())) throw new AppException(ErrorCode.UNAUTHORIZED);

        if (order.getStatus().equals(OrderStatus.PENDING)) processCancellation(order, request.getReason(), "User: ");
        else throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
    }


    @Transactional
    @Override
    public void cancelOrderAsAdmin(Long orderId, CancelOrderRequest request) {
        Order order = getOrderEntity(orderId);
        if (order.getStatus().equals(OrderStatus.PENDING) || order.getStatus().equals(OrderStatus.CONFIRMED)
                || order.getStatus().equals(OrderStatus.PROCESSING)) {
            processCancellation(order, request.getReason(), "Admin: ");
        } else throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
    }

    private Order getOrderEntity(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }

    private void processCancellation(Order order, String reason, String prefix) {
        order.setStatus(OrderStatus.CANCELLED);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .status(OrderStatus.CANCELLED)
                .note(prefix + " Lý do: " + reason).build();
        order.addStatusHistory(history);

        for (OrderItem item : order.getOrderItems()) {
            Long updated = productVariantRepository.addStock(item.getProductVariant().getId(), Long.valueOf(item.getQuantity()));
            if (updated == 0) throw new AppException(ErrorCode.FAILED_TO_UPDATE_STOCK);
        }
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void updateStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = getOrderEntity(orderId);
        if (order.getStatus().equals(OrderStatus.CANCELLED) || order.getStatus().equals(OrderStatus.RETURNED))
            throw new AppException(ErrorCode.ORDER_ALREADY_FINALIZED);

        if (order.getStatus().equals(request.getStatus()))
            throw new AppException(ErrorCode.STATUS_NOT_CHANGED);

        if (request.getStatus().equals(OrderStatus.CANCELLED))
            throw new AppException(ErrorCode.INVALID_STATUS_UPDATE_USE_CANCEL_API);
        order.setStatus(request.getStatus());

        OrderStatusHistory history = OrderStatusHistory.builder()
                .status(request.getStatus())
                .note(request.getNote()).build();
        order.addStatusHistory(history);

        //Todo: Không cho phép nhảy từ PENDING thẳng lên DELIVERED)
        orderRepository.save(order);
    }

    @Override
    public void handlePaymentCallback(String orderCode, boolean isSuccess) {

    }

    @Transactional
    @Override
    public void updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        Order order = getOrderEntity(orderId);
        order.setPaymentStatus(paymentStatus);
    }


    // Dung requires new de moi don hang la mot transaction doc lap
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void cancelOrderSystem(Order order) {
        if(order.getStatus() != OrderStatus.PENDING) return;
        if(order.getPaymentMethod() == PaymentMethod.COD) return;
        if(order.getPaymentStatus() != PaymentStatus.UNPAID) return;
        order.setStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.EXPIRED);

        // roll back stock
        for (OrderItem item : order.getOrderItems()){
            long updated = productVariantRepository.addStock(item.getProductVariant().getId(), Long.valueOf(item.getQuantity()));
            if(updated == 0){
                throw new AppException(ErrorCode.FAILED_TO_UPDATE_STOCK);
            }
        }
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(OrderStatus.CANCELLED)
                .note("System: Tự động hủy đơn do quá hạn thanh toán VNPay")
                .build();
        order.addStatusHistory(history);
        orderRepository.save(order);
    }


}
