package com.nguyenhuugiap.computer_shop.listener;

import com.nguyenhuugiap.computer_shop.event.PaymentSuccessEvent;
import com.nguyenhuugiap.computer_shop.service.interfaces.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final OrderService orderService;

    // Use TransactionalEventListener to ensure this only runs AFTER the IPN transaction is successfully committed to DB.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentSuccessEvent(PaymentSuccessEvent event) {
        Long orderId = event.getOrderId();
        log.info("Received PaymentSuccessEvent. Triggering auto-confirm for Order ID: {}", orderId);
        try {
            // Call the Gatekeeper method
            orderService.tryAutoConfirm(orderId);
        } catch (Exception e) {
            // Catch here to prevent the event from crashing the main flow (though AFTER_COMMIT runs independently)
            log.error("Failed to auto-confirm Order ID: {} after payment success.", orderId, e);
        }
    }
}