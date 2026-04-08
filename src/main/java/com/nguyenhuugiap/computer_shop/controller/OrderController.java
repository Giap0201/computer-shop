package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.order.CancelOrderRequest;
import com.nguyenhuugiap.computer_shop.dto.order.OrderCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.order.OrderResponse;
import com.nguyenhuugiap.computer_shop.service.interfaces.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderCreationRequest request) {
        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long orderId) {
        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderById(orderId))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{orderCode}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByOrderCode(@PathVariable String orderCode) {
        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderByCode(orderCode))
                .build();
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Void>> userCancelOrder(@PathVariable Long orderId, @RequestBody CancelOrderRequest request) {
        orderService.cancelOrderAsUser(orderId, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder().message("Huỷ đơn thành công!")
                .build());
    }

}
