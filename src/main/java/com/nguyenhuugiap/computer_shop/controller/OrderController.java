package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.order.OrderCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.order.OrderResponse;
import com.nguyenhuugiap.computer_shop.service.interfaces.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderCreationRequest request){
        ApiResponse<OrderResponse> response = ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
