package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.category.CategoryCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.category.CategoryUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryCreationRequest request);

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getCategoryRoots();

    CategoryResponse getCategoryById(long id);

    void deleteCategoryById(long id);

    CategoryResponse updateCategory(Long id, CategoryUpdateRequest request);
}
