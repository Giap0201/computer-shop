package com.nguyenhuugiap.computer_shop.configuration;

import com.nguyenhuugiap.computer_shop.service.interfaces.AuthenticateService;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    AuthenticateService authenticateService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        try {
            JWTClaimsSet jwtClaimsSet = authenticateService.verifyToken(token);
            String userId = jwtClaimsSet.getSubject();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (Exception e) {
            log.error("JWT Authentication Failed: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
