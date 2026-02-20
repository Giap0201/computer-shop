package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.authenticate.AuthenticateRequest;
import com.nguyenhuugiap.computer_shop.dto.authenticate.IntrospectRequest;
import com.nguyenhuugiap.computer_shop.dto.authenticate.LogoutRequest;
import com.nguyenhuugiap.computer_shop.dto.authenticate.RefreshTokenRequest;
import com.nguyenhuugiap.computer_shop.dto.authenticate.AuthenticateResponse;
import com.nguyenhuugiap.computer_shop.dto.authenticate.IntrospectResponse;
import com.nguyenhuugiap.computer_shop.entity.InvalidatedToken;
import com.nguyenhuugiap.computer_shop.entity.User;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.repository.InvalidatedTokenRepository;
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
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticateServiceImpl implements AuthenticateService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    InvalidatedTokenRepository invalidatedTokenRepository;
    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.refresh-signer-key}")
    protected String REFRESH_SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    private long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    private long REFRESHABLE_DURATION;


    @Override
    public String generateToken(User user, long expiryTime, String key) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId().toString())
                .issuer("nguyenhuugiap.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(expiryTime, ChronoUnit.SECONDS).toEpochMilli()))
                .claim("scope", buildScope(user))
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            byte[] byteKeys = Base64.getDecoder().decode(key);
            jwsObject.sign(new MACSigner(byteKeys));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Token generation failed", e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        User user = userRepository.findByEmail(request.getUsername()).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        String accessToken = generateToken(user, VALID_DURATION, SIGNER_KEY);
        String refreshToken = generateToken(user, REFRESHABLE_DURATION, REFRESH_SIGNER_KEY);
        return AuthenticateResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        boolean isValid = true;
        try {
            verifyToken(request.getToken(), SIGNER_KEY);
        } catch (AppException | JOSEException | ParseException e) {
            log.error("Token validation failed", e);
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid).build();
    }

    @Override
    public JWTClaimsSet verifyToken(String token, String key) throws JOSEException, ParseException {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(keyBytes);
        boolean verified = signedJWT.verify(verifier);
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (!(verified && expirationDate.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        if (invalidatedTokenRepository.existsById(jti)) {
            log.warn("Invalid token");
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT.getJWTClaimsSet();
    }


    @Override
    public void logout(LogoutRequest request) {
        invalidateToken(request.getToken());
        invalidateToken(request.getRefreshToken());
    }

    private void invalidateToken(String token) {
        if(token == null || token.isBlank()) return;
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jti)
                    .expires(expirationDate)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

    }

    @Override
    public AuthenticateResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        JWTClaimsSet jwtClaimsSet = verifyToken(request.getToken(), REFRESH_SIGNER_KEY);
        Date expirationDate = jwtClaimsSet.getExpirationTime();
        String jti = jwtClaimsSet.getJWTID();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .expires(expirationDate)
                .id(jti)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
        Long id = Long.parseLong(jwtClaimsSet.getSubject());
        User user = userRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND));
        String accessToken = generateToken(user, VALID_DURATION, SIGNER_KEY);
        String refreshToken = generateToken(user, REFRESHABLE_DURATION, REFRESH_SIGNER_KEY);
        return AuthenticateResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    private String buildScope(User user) {
        StringJoiner scopeJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                String roleName = role.getName();
                if (!roleName.startsWith("ROLE_")) {
                    roleName = "ROLE_" + roleName;
                }
                scopeJoiner.add(roleName);
            });
        }
        return scopeJoiner.toString();
    }
}
