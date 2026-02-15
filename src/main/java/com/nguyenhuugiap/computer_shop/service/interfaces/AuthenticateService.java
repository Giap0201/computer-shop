package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.request.AuthenticateRequest;
import com.nguyenhuugiap.computer_shop.dto.request.IntrospectRequest;
import com.nguyenhuugiap.computer_shop.dto.request.LogoutRequest;
import com.nguyenhuugiap.computer_shop.dto.response.AuthenticateResponse;
import com.nguyenhuugiap.computer_shop.dto.response.IntrospectResponse;
import com.nguyenhuugiap.computer_shop.entity.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import java.text.ParseException;

public interface AuthenticateService {
    String generateToken(User user);

    AuthenticateResponse authenticate(AuthenticateRequest request);

    IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException;

    JWTClaimsSet verifyToken(String token) throws JOSEException, ParseException;

    void logout(LogoutRequest request) throws ParseException;
}
