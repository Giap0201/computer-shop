package com.nguyenhuugiap.computer_shop.security;

import com.nguyenhuugiap.computer_shop.configuration.CustomAccessDeniedHandler;
import com.nguyenhuugiap.computer_shop.configuration.JwtAuthenticationEntryPoint;
import com.nguyenhuugiap.computer_shop.configuration.JwtAuthenticationFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    String[] PUBLIC_ENDPOINTS = {"/users/**", "/auth/**"};
    String[] CATEGORIES_PUBLIC_ENDPOINTS = {"/categories/**", "/brands/**", "/uploads/**"};
    String[] PRODUCTS_PUBLIC_ENDPOINTS = {"/products/**", "/attributes/**", "/redis-test/**"};

    JwtAuthenticationFilter jwtAuthenticationFilter;
    CustomAccessDeniedHandler customAccessDeniedHandler;
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth ->
                        auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                // Swagger
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs",
                                        "/webjars/**"
                                ).permitAll()

                                // Public Endpoints
                                .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                                .requestMatchers(HttpMethod.GET, PRODUCTS_PUBLIC_ENDPOINTS).permitAll()
                                .requestMatchers(HttpMethod.GET, CATEGORIES_PUBLIC_ENDPOINTS).permitAll()

                                // Cart Endpoints (Đã sửa thêm dấu /)
                                .requestMatchers(HttpMethod.POST, "/carts/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/carts/**").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/carts/**").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/carts/**").permitAll()

                                // Payment callbacks
                                .requestMatchers("/payments/vnpay-return", "/payments/vnpay-ipn").permitAll()

                                // Admin paths
                                .requestMatchers(HttpMethod.POST, "/files/**").hasRole("ADMIN")

                                .requestMatchers("/login/oauth2/**", "/oauth2/**").permitAll()

                                .anyRequest().authenticated())
                .oauth2Login(oath2 -> oath2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.exceptionHandling(exceptionHandler -> exceptionHandler
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler));

        return httpSecurity.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3002"));

        // Cho phép tất cả các header và method
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        // QUAN TRỌNG: Phải có dòng này thì Frontend mới gửi được Cookie CART_SESSION_ID lên
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}