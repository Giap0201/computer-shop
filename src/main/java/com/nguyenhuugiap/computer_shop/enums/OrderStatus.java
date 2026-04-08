package com.nguyenhuugiap.computer_shop.enums;

public enum OrderStatus {
    PENDING,      // Chờ xác nhận (Khách vừa đặt xong)
    CONFIRMED,    // Đã xác nhận (Shop gọi điện chốt đơn OK, hoặc đã thanh toán)
    PROCESSING,   // Shop đang nhặt hàng, đóng gói, in vận đơn.
    SHIPPING,     // Đang giao hàng (Đã bàn giao cho Shipper / J&T / GHTK)
    DELIVERED,    // Shipper báo giao xong, nhưng chưa chốt sổ (Khách còn 3-7 ngày để test máy, đổi trả)
    COMPLETED,    // Hoàn thành: Khách bấm "Đã nhận được hàng" hoặc hết hạn đổi trả. Kế toán ghi nhận doanh thu.
    CANCELLED,    // Đã hủy
    RETURNED      // Khách trả lại cho shop.
}