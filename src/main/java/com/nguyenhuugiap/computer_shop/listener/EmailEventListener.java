package com.nguyenhuugiap.computer_shop.listener;

import com.nguyenhuugiap.computer_shop.entity.Order;
import com.nguyenhuugiap.computer_shop.event.OrderPlaceEvent;
import com.nguyenhuugiap.computer_shop.event.PaymentSuccessEvent;
import com.nguyenhuugiap.computer_shop.repository.OrderRepository;
import com.nguyenhuugiap.computer_shop.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final OrderRepository orderRepository;
    private final EmailService emailService;

    // Đẩy tác vụ gửi mail sang một Thread khác để không làm chậm luồng chính
    @Async("taskExecutor")
    // Chỉ kích hoạt khi luồng Tiền (IPN) đã thực sự Commit vào Database thành công
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentSuccessForEmail(PaymentSuccessEvent event) {
        Long orderId = event.getOrderId();

        try {
            // Lấy lại đơn hàng từ DB để lấy Email của khách
            Order order = orderRepository.findByIdWithUser(orderId).orElse(null);

            if (order != null && order.getUser() != null) {
                String customerEmail = order.getUser().getEmail();

                // Gọi người đưa thư
                emailService.sendOrderConfirmation(customerEmail, order.getOrderCode());
            }
        } catch (Exception e) {
            log.error("LỖI CRITICAL: Gửi email thất bại cho đơn {}. Nguyên nhân: {}", orderId, e.getMessage());
        }
    }

    @Async("taskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPlacedForEmail(OrderPlaceEvent event){
        Long orderId = event.getOrderId();
        try {
            Order order = orderRepository.findByIdWithUser(orderId).orElse(null);

            if (order != null && order.getUser() != null) {
                String customerEmail = order.getUser().getEmail();
                emailService.sendCodOrderConfirmation(customerEmail, order.getOrderCode());
            }
        }catch (Exception e){
            log.error("LỖI CRITICAL: Gửi email thất bại cho đơn {}. Nguyên nhân: {}", orderId, e.getMessage());
        }
    }
}