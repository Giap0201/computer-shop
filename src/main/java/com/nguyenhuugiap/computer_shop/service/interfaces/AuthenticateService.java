package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.authenticate.AuthenticateRequest;
import com.nguyenhuugiap.computer_shop.dto.authenticate.IntrospectRequest;
import com.nguyenhuugiap.computer_shop.dto.authenticate.LogoutRequest;
import com.nguyenhuugiap.computer_shop.dto.authenticate.RefreshTokenRequest;
import com.nguyenhuugiap.computer_shop.dto.authenticate.AuthenticateResponse;
import com.nguyenhuugiap.computer_shop.dto.authenticate.IntrospectResponse;
import com.nguyenhuugiap.computer_shop.entity.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import java.text.ParseException;

public interface AuthenticateService {
    String generateToken(User user, long expiryTime, String key);

    AuthenticateResponse authenticate(AuthenticateRequest request);

    IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException;

    JWTClaimsSet verifyToken(String token, String key) throws JOSEException, ParseException;

    void logout(LogoutRequest request) throws ParseException;
    AuthenticateResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException;
}
