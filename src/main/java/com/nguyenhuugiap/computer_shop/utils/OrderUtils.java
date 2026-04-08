package com.nguyenhuugiap.computer_shop.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderUtils {
    private static final String CHARACTERS = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    private static final int RANDOM_STRING_LENGTH = 4; // Độ dài đuôi ngẫu nhiên
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateOrderCode() {
        // 1. Lấy ngày giờ hiện tại theo format YYMMDD
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String dateString = now.format(formatter); // Ví dụ: 260330

        // 2. Sinh chuỗi ngẫu nhiên 4 ký tự
        StringBuilder randomString = new StringBuilder(RANDOM_STRING_LENGTH);
        for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            randomString.append(CHARACTERS.charAt(randomIndex));
        }

        // 3. Ghép lại
        return "ORD-" + dateString + "-" + randomString.toString();
    }
}