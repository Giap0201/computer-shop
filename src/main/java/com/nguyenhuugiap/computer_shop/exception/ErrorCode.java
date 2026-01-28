package com.nguyenhuugiap.computer_shop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor

public enum ErrorCode {
    // SYSTEM(1XXX)
    UNCATEGORIZED_EXCEPTION(1001, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1002, "Invalid error key", HttpStatus.BAD_REQUEST), // Loi sai key
    VALIDATION_FAILED(1003, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    // AUTH (2XXX)
    //USER (3XXX)
    USER_EXISTS(3001, "Đã tồn tại user", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(3002, "Không tồn tại user", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(3003, "Tên không đúng định dạng", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(3004, "Mật khẩu không đúng định dạng", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(3005, "Email không đúng định dạng", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(3006, "Số điện thoại không đúng định dạng", HttpStatus.BAD_REQUEST),

    //ROLE (35xx))
    ROLE_NOT_FOUND(3501, "Không tìm thấy role", HttpStatus.NOT_FOUND),


    //BRAND (4xxx)
    BRAND_EXISTS(4001, "Thương hiệu đã tồn tại", HttpStatus.BAD_REQUEST),

    //CATEGORY (45XX)
    CATEGORY_NAME_REQUIRED(4501, "Tên danh mục không được bỏ trống", HttpStatus.BAD_REQUEST),
    CATEGORY_TOO_LONG(4502, "Tên danh mục tối đa 100 kí tự", HttpStatus.BAD_REQUEST),
    CATEGORY_EXISTS(4503, "Tên danh mục đã tồn tại", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(4504, "Danh mục cha không tồn tại", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_HAS_CHILDREN(4505, "Không thể xoá danh mục do đang có con", HttpStatus.BAD_REQUEST),
    CANNOT_UPDATE_CATEGORY(4506, "Không thể cập nhật danh mục ", HttpStatus.BAD_REQUEST),
    ;
    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;
}
