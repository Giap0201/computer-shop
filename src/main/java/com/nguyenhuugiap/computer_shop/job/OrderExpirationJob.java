package com.nguyenhuugiap.computer_shop.job;

import com.nguyenhuugiap.computer_shop.entity.Order;
import com.nguyenhuugiap.computer_shop.repository.OrderRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderExpirationJob {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Scheduled(cron = "0 * * * * *")
    public void processExpiredOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);

        List<Order> expiredOrders = orderRepository.findExpiredOrders(threshold);

        if (!expiredOrders.isEmpty()) {
            log.info("Found {} expired orders to cancel.", expiredOrders.size());
        }
        for (Order order : expiredOrders) {
            try {
                orderService.cancelOrderSystem(order);
            } catch (Exception e) {
                log.error("Lỗi khi hủy đơn hàng hết hạn: {}", order.getOrderCode(), e);
            }
        }
    }
}