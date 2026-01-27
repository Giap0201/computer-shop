package com.nguyenhuugiap.computer_shop.repository;

import com.nguyenhuugiap.computer_shop.dto.response.CategoryResponse;
import com.nguyenhuugiap.computer_shop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    // Gay ra loi N+1 query
//    List<Category> findByParentIsNull();
    // Su dung Join Fetch
    @Query("select distinct c from Category  c left join fetch c.children where c.parent is null ")
    List<Category> findAllRoots();

    boolean existsByParentId(long id);
}
