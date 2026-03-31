package com.nguyenhuugiap.computer_shop.controller.admin;

import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.order.CancelOrderRequest;
import com.nguyenhuugiap.computer_shop.dto.order.UpdateOrderStatusRequest;
import com.nguyenhuugiap.computer_shop.service.interfaces.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminOrderController {
    OrderService orderService;

    @PatchMapping("{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateOrderStatus(@PathVariable Long orderId,
                                                               @Valid @RequestBody UpdateOrderStatusRequest request) {
        orderService.updateStatus(orderId, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder().message("Cập nhật trạng thái đơn hàng thành công")
                .build());
    }

    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> adminCancelOrder(@PathVariable Long orderId,
                                                              @RequestBody CancelOrderRequest request) {
        orderService.cancelOrderAsAdmin(orderId, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder().message("Huỷ đơn thành công!")
                .build());
    }
}
