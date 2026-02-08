package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.request.AuthenticateRequest;
import com.nguyenhuugiap.computer_shop.dto.request.IntrospectRequest;
import com.nguyenhuugiap.computer_shop.dto.response.AuthenticateResponse;
import com.nguyenhuugiap.computer_shop.dto.response.IntrospectResponse;
import com.nguyenhuugiap.computer_shop.entity.User;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.repository.UserRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.AuthenticateService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticateServiceImpl implements AuthenticateService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @Override
    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .issuer("nguyenhuugiap.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("scope", "ROLE_USER")
                .claim("jti", UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            byte[] keyBytes = Base64.getDecoder().decode(SIGNER_KEY);
            jwsObject.sign(new MACSigner(keyBytes));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error while generating token", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        User user = userRepository.findByEmail(request.getUsername()).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND));
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        if (!authenticated) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }
        String token = generateToken(user);
        return AuthenticateResponse.builder()
                .token(token)
                .authenticated(true).build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        String token = request.getToken();
        SignedJWT signedJWT = SignedJWT.parse(token);
        byte[] keyBytes = Base64.getDecoder().decode(SIGNER_KEY);
        JWSVerifier verifier = new MACVerifier(keyBytes);
        boolean verified = signedJWT.verify(verifier);
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        return IntrospectResponse.builder()
                .valid(verified && expirationDate.after(new Date())).build();
    }
}
