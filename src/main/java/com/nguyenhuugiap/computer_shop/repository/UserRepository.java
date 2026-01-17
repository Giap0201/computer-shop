package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // tim user theo email
    Optional<User> findByEmail(String email);
    // Kiem tra xem email da ton tai chua
    boolean existsByEmail(String email);
}
