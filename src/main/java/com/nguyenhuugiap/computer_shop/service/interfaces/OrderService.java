package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.order.OrderCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.order.OrderResponse;
import com.nguyenhuugiap.computer_shop.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    // Luồng chính
    OrderResponse createOrder(OrderCreationRequest request);

    // Luồng tra cứu
    OrderResponse getOrderById(Long orderId);
    OrderResponse getOrderByCode(String orderCode); // Thường dùng Code để tra cứu tiện hơn ID
    Page<OrderResponse> getMyOrders(Pageable pageable);

    // Luồng quản lý vận hành
    void cancelOrder(Long orderId, String reason);
    void updateStatus(Long orderId, OrderStatus newStatus, String note);
    void handlePaymentCallback(String orderCode, boolean isSuccess);

}
