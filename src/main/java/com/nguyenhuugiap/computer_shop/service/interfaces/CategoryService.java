package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.request.CategoryCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.request.CategoryUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryCreationRequest request);

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getCategoryRoots();

    CategoryResponse getCategoryById(long id);

    void deleteCategoryById(long id);

    CategoryResponse updateCategory(Long id, CategoryUpdateRequest request);
}
