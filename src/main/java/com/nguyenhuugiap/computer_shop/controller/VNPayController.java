package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.service.impl.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayController {

    PaymentService paymentService;

    @GetMapping("/create-url")
    public ResponseEntity<ApiResponse<String>> createPaymentUrl(
            @RequestParam String orderCode,
            HttpServletRequest request) {
        String paymentUrl = paymentService.createPaymentUrl(orderCode, request);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .result(paymentUrl)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String redirectUrl = paymentService.processReturn(request);
        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> vnpayIpn(HttpServletRequest request) {
        Map<String, String> result = paymentService.processIpn(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/verify-status/{pnrCode}")
    public ResponseEntity<ApiResponse<Map<String, String>>> verifyPaymentStatusImmediately(@PathVariable String orderCode) {
        Map<String, String> result = paymentService.verifyPaymentStatusImmediately(orderCode);
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .result(result)
                .build();
        return ResponseEntity.ok(response);
    }
}