package com.nguyenhuugiap.computer_shop.exception;

import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handingAppException(AppException appException) {
        ErrorCode errorCode = appException.getErrorCode();
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(RuntimeException runtimeException) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .build();
        log.error(runtimeException.getMessage(), runtimeException);
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION
                .getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiResponse<?>> handlingMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(ErrorCode.VALIDATION_FAILED.getCode())
                .message(ErrorCode.VALIDATION_FAILED.getMessage())
                .build();
        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED
                .getHttpStatusCode()).body(apiResponse);
    }


    // Bat cac loi do minh tu dinh nghia validation
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String enumKey = exception.getBindingResult().getFieldError().getDefaultMessage();
        // Neu khong tim thay Key trong Enum thi dung loi mac dinh
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try {
            // Chuyen string thanh Enum ErrorCode
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid error key from validation: {}", enumKey);
        }
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = {
            AccessDeniedException.class,
            AuthorizationDeniedException.class
    })
    ResponseEntity<ApiResponse<?>> handingAccessDeniedException(AccessDeniedException exception) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .message(ErrorCode.UNAUTHORIZED.getMessage())
                .build();
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getHttpStatusCode()).body(apiResponse);
    }
}
