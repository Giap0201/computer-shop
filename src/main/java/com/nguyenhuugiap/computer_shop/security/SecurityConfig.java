package com.nguyenhuugiap.computer_shop.security;

import com.nguyenhuugiap.computer_shop.configuration.JwtAuthenticationFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SecurityConfig {

    String[] PUBLIC_ENDPOINTS = {"/users/**", "/auth/login", "/auth/token", "/auth/introspect"};
    String[] CATEGORIES_PUBLIC_ENDPOINTS = {"/categories/**", "/brands/**"};
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth ->
                        auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
//                                .requestMatchers(HttpMethod.GET, CATEGORIES_PUBLIC_ENDPOINTS).permitAll()
                                .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
