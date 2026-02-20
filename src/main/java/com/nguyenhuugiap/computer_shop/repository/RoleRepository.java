package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);
}
