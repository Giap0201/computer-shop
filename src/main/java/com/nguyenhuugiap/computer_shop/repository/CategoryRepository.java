package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
}
