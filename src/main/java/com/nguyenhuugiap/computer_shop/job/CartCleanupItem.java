package com.nguyenhuugiap.computer_shop.job;


import com.nguyenhuugiap.computer_shop.service.interfaces.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartCleanupItem {
    CartService cartService;

//    @Scheduled(fixedRate = 5000)
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredCartGuest() {
        log.info("Cleaning up cart of user");
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        cartService.deleteGuestCarts(threshold);
        log.info("Cleaning up cart of user done");
    }
}
