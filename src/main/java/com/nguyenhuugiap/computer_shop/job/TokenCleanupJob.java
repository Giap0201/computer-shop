package com.nguyenhuugiap.computer_shop.job;

import com.nguyenhuugiap.computer_shop.repository.InvalidatedTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenCleanupJob {
    InvalidatedTokenRepository invalidatedTokenRepository;

//    @Scheduled(fixedRate = 5000)
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTokens() {
        log.info("Cleaning up expired tokens start");
        invalidatedTokenRepository.deleteAllExpiredSince(new Date());
        log.info("Cleaning up expired tokens done");
    }}
