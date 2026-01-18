package com.nguyenhuugiap.computer_shop.configuration;

import com.nguyenhuugiap.computer_shop.entity.Role;
import com.nguyenhuugiap.computer_shop.entity.User;
import com.nguyenhuugiap.computer_shop.enums.RoleType;
import com.nguyenhuugiap.computer_shop.repository.RoleRepository;
import com.nguyenhuugiap.computer_shop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InitApplicationConfig {
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    ApplicationRunner applicationRunner() {
        return args -> {
            log.info("Initializing application");
            // Khoi tao role USER neu chua co
            if (roleRepository.findByName(RoleType.USER.name()).isEmpty()) {
                Role userRole = Role.builder()
                        .name(RoleType.USER.name())
                        .description("Người dùng cơ bản")
                        .build();
                roleRepository.save(userRole);
                log.info("User role created");
            }
            // Khoi tao role ADMIN neu chua co
            if (roleRepository.findByName(RoleType.ADMIN.name()).isEmpty()) {
                Role adminRole = Role.builder()
                        .name(RoleType.ADMIN.name())
                        .description("Quản trị viên hệ thống")
                        .build();
                roleRepository.save(adminRole);
                log.info("Admin role created");
            }

            // Tao ra 1 admin he thong
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = User.builder()
                        .email("admin@gmail.com")
                        .passwordHash(passwordEncoder.encode("admin"))
                        .phone("0123456789")
                        .build();
                admin = userRepository.save(admin);
                Role adminRole = roleRepository.findByName(RoleType.ADMIN.name()).get();
                admin.addRole(adminRole);
                userRepository.save(admin);
                log.info("Admin user created with password is admin, please change your new password");
            }
            log.info("Initializing application complete");
        };
    }
}
