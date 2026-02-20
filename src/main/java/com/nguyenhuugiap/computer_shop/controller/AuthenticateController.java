package com.nguyenhuugiap.computer_shop.controller;

import com.nguyenhuugiap.computer_shop.dto.authenticate.AuthenticateRequest;
import com.nguyenhuugiap.computer_shop.dto.authenticate.IntrospectRequest;
import com.nguyenhuugiap.computer_shop.dto.authenticate.LogoutRequest;
import com.nguyenhuugiap.computer_shop.dto.authenticate.RefreshTokenRequest;
import com.nguyenhuugiap.computer_shop.dto.ApiResponse;
import com.nguyenhuugiap.computer_shop.dto.authenticate.AuthenticateResponse;
import com.nguyenhuugiap.computer_shop.dto.authenticate.IntrospectResponse;
import com.nguyenhuugiap.computer_shop.service.interfaces.AuthenticateService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticateController {
    AuthenticateService authenticateService;

    @PostMapping("/login")
    public ApiResponse<AuthenticateResponse> authenticate(@RequestBody AuthenticateRequest authenticateRequest) {
        return ApiResponse.<AuthenticateResponse>builder()
                .result(authenticateService.authenticate(authenticateRequest))
                .build();
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
