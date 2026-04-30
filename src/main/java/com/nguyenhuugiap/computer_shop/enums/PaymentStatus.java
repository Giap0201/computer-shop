package com.nguyenhuugiap.computer_shop.enums;

public enum PaymentStatus {
    UNPAID,       // Chưa thanh toán (vừa tạo đơn)
    PROCESSING,   // Đang xử lý (Khách đã click thanh toán, đang ở trang VNPay, chờ IPN dội về)
    PAID,         // Đã thanh toán thành công
    EXPIRED,    // không thanh toán
    REFUNDED      // Đã hoàn tiền (Hoàn toàn bộ)
}
