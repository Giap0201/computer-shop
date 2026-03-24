package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.authenticate.*;
import com.nguyenhuugiap.computer_shop.security.CookieUtils;
import com.nguyenhuugiap.computer_shop.service.interfaces.AuthenticateService;
import com.nguyenhuugiap.computer_shop.service.interfaces.CartService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticateController {
    AuthenticateService authenticateService;
    CookieUtils cookieUtils;
    CartService cartService;

    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthenticateResponse>> authenticate(
            @RequestBody @Valid AuthenticateRequest request,
            @CookieValue(value = CookieUtils.CART_SESSION_COOKIE_NAME, required = false) String sessionId) {
        AuthenticateResponse authResponse = authenticateService.authenticate(request);

        if (authResponse.isAuthenticated()) {
            cartService.mergeCart(sessionId, authResponse.getUserId());
        }
        ResponseCookie deleteCookie = cookieUtils.deleteCartSessionCookie();
        ApiResponse<AuthenticateResponse> response = ApiResponse.<AuthenticateResponse>builder()
                .result(authResponse)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(response);

    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        return ApiResponse.<IntrospectResponse>builder()
                .result(authenticateService.introspect(introspectRequest))
                .build();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> logout(@RequestBody LogoutRequest logoutRequest) throws ParseException {
        authenticateService.logout(logoutRequest);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticateResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) throws ParseException, JOSEException {
        return ApiResponse.<AuthenticateResponse>builder()
                .result(authenticateService.refreshToken(refreshTokenRequest))
                .build();
    }
}
