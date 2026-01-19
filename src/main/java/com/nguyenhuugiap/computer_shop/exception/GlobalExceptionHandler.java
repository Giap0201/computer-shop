package com.nguyenhuugiap.computer_shop.exception;

import com.nguyenhuugiap.computer_shop.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Bat cac loi ngiep vu do minh tao ra
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handingAppException(AppException appException) {
        ErrorCode errorCode = appException.getErrorCode();
        ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }


    // Bat cac loi he thong khong luong truoc
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handingRuntimeException(RuntimeException runtimeException) {
        ApiResponse apiResponse = ApiResponse.builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .build();
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION
                .getHttpStatusCode()).body(apiResponse);
    }

    // Bat cac loi do minh tu dinh nghia validation
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        // Lay message dinh danh loi
        String enumKey = exception.getBindingResult().getFieldError().getDefaultMessage();
        // Neu khong tim thay Key trong Enum thi dung loi mac dinh
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try {
            // Chuyen string thanh Enum ErrorCode
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {

        }
        ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }
}
