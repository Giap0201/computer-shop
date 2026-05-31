package com.nguyenhuugiap.computer_shop.security;

import com.nguyenhuugiap.computer_shop.entity.Role;
import com.nguyenhuugiap.computer_shop.entity.User;
import com.nguyenhuugiap.computer_shop.enums.AuthProvider;
import com.nguyenhuugiap.computer_shop.enums.UserStatus;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.repository.RoleRepository;
import com.nguyenhuugiap.computer_shop.repository.UserRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.AuthenticateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Set;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    UserRepository userRepository;
    RoleRepository roleRepository;
    AuthenticateService authenticateService;

    @NonFinal
    @Value("${jwt.signer-key}")
    String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.refresh-signer-key}")
    String REFRESH_SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    long REFRESHABLE_DURATION;

    @NonFinal
    @Value("${app.oauth2.redirect-uri}")
    String REDIRECT_URI;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String providerId = oAuth2User.getAttribute("sub");

        log.info("Khách hàng đăng nhập qua Google: {}", email);

        User user = userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, name, picture, providerId))
                .orElseGet(() -> createNewUser(email, name, picture, providerId));

        String accessToken = authenticateService.generateToken(user, VALID_DURATION, SIGNER_KEY);
        String refreshToken = authenticateService.generateToken(user, REFRESHABLE_DURATION, REFRESH_SIGNER_KEY);

        String targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
                .queryParam("token", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }


    private User updateExistingUser(User existingUser, String name, String picture, String providerId) {
        if (existingUser.getAuthProvider() == null || existingUser.getAuthProvider() == AuthProvider.LOCAL) {
            existingUser.setAuthProvider(AuthProvider.GOOGLE);
            existingUser.setProviderId(providerId);
            if (name != null && !name.isEmpty()) {
                existingUser.setFullName(name);
            }
            existingUser.setAvatarUrl(picture);
            return userRepository.save(existingUser);
        }
        return existingUser;
    }

    private User createNewUser(String email, String name, String picture, String providerId) {
        Role role = roleRepository.findByName("USER").orElseThrow(() ->
                new AppException(ErrorCode.ROLE_NOT_FOUND));

        User newuser = User.builder()
                .email(email)
                .fullName(name)
                .authProvider(AuthProvider.GOOGLE)
                .providerId(providerId)
                .status(UserStatus.ACTIVE)
                .roles(Set.of(role))
                .avatarUrl(picture)
                .build();
        return userRepository.save(newuser);

    }

}
