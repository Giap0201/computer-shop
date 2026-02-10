package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<User> findByPhone(String phone);

    @Query("SELECT u from User u left join fetch u.roles where u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);
    Optional<User> findByFullName(String fullName);
}
