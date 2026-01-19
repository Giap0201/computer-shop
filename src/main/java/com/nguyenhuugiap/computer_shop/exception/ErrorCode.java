package com.nguyenhuugiap.computer_shop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@NoArgsConstructor

public enum ErrorCode {
    // SYSTEM(1XXX)
    UNCATEGORIZED_EXCEPTION(1001, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY (1002, "Invalid error key", HttpStatus.BAD_REQUEST), // Loi sai key

    // AUTH (2XXX)
    //USER (3XXX)
    USER_EXISTS(3001, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(3002, "User not found", HttpStatus.NOT_FOUND),
    USERNAME_INVALID(3003, "Username invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(3004, "Password invalid", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(3005, "Email invalid", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(1007, "Phone invalid", HttpStatus.BAD_REQUEST),

    //ROLE (35xx))
    ROLE_NOT_FOUND(3501, "Role not found", HttpStatus.NOT_FOUND),
    ;

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;
}
