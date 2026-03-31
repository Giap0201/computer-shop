package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.order.CancelOrderRequest;
import com.nguyenhuugiap.computer_shop.dto.order.OrderCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.order.OrderResponse;
import com.nguyenhuugiap.computer_shop.dto.order.UpdateOrderStatusRequest;
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
    void cancelOrderAsUser(Long orderId, CancelOrderRequest request);
    void cancelOrderAsAdmin(Long orderId, CancelOrderRequest request);
    void updateStatus(Long orderId, UpdateOrderStatusRequest request);
    void handlePaymentCallback(String orderCode, boolean isSuccess);

}
