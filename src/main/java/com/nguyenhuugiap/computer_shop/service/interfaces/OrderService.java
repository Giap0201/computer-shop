package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.PageResponse;
import com.nguyenhuugiap.computer_shop.dto.order.*;
import com.nguyenhuugiap.computer_shop.entity.Order;
import com.nguyenhuugiap.computer_shop.enums.PaymentStatus;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    // Luồng chính
    OrderResponse createOrder(OrderCreationRequest request);

    // Luồng tra cứu
    OrderResponse getOrderById(Long orderId);
    OrderResponse getOrderByCode(String orderCode); // Thường dùng Code để tra cứu tiện hơn ID
    PageResponse<OrderResponse> getMyOrders(Pageable pageable);
    Order getOrderEntityByCode(String orderCode);

    PageResponse<OrderResponse> getAllOrderAsAdmin(AdminOrderSearchRequest request, int page, int size);
    // Luồng quản lý vận hành
    void cancelOrderAsUser(Long orderId, CancelOrderRequest request);
    void cancelOrderAsAdmin(Long orderId, CancelOrderRequest request);
    void updateStatus(Long orderId, UpdateOrderStatusRequest request);
    void handlePaymentCallback(String orderCode, boolean isSuccess);

    void updatePaymentStatus(Long orderId, PaymentStatus paymentStatus);
}
