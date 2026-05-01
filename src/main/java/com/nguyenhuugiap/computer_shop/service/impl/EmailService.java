package com.nguyenhuugiap.computer_shop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendOrderConfirmation(String toEmail, String orderCode) {
        log.info("Đang tiến hành gửi email xác nhận đến: {}", toEmail);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("giapit02012005@gmail.com");

        message.setTo(toEmail);
        message.setSubject("Xác nhận thanh toán thành công - Đơn hàng " + orderCode);
        message.setText("Chào bạn,\n\n" +
                "Cảm ơn bạn đã mua sắm tại Computer Shop! Đơn hàng " + orderCode + " của bạn đã được thanh toán thành công qua VNPay.\n" +
                "Chúng tôi đang tiến hành đóng gói và sẽ giao đến bạn trong thời gian sớm nhất.\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ Computer Shop.");

        mailSender.send(message);
        log.info("Đã gửi email thành công!");
    }

    public void sendCodOrderConfirmation(String toEmail, String orderCode){
        log.info("Đang tiến hành gửi email xác nhận đến: {}", toEmail);

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("giapit02012005@gmail.com");

        message.setTo(toEmail);
        message.setSubject("Xác nhận đặt hàng thành công - Đơn hàng " + orderCode);
        message.setText("Chào bạn,\n\n" +
                "Cảm ơn bạn đã mua sắm tại Computer Shop! Đơn hàng " + orderCode + " của bạn đã được xác nhận thành công\n" +
                "Chúng tôi đang tiến hành đóng gói và sẽ giao đến bạn trong thời gian sớm nhất.\n\n" +
                "Trân trọng,\n" +
                "Đội ngũ Computer Shop.");

        mailSender.send(message);
        log.info("Đã gửi email thành công!");
    }
}