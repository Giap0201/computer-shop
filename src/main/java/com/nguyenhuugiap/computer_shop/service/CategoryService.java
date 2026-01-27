package com.nguyenhuugiap.computer_shop.service;

import com.nguyenhuugiap.computer_shop.dto.request.CategoryRequest;
import com.nguyenhuugiap.computer_shop.dto.response.CategoryResponse;
import com.nguyenhuugiap.computer_shop.entity.Category;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.mapper.CategoryMapper;
import com.nguyenhuugiap.computer_shop.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTS);
        }
        Category category = categoryMapper.toEntity(request);
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            category.setParent(parent);
        }
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryRoots(){
        return categoryRepository.findAllRoots()
                .stream().map(categoryMapper::toResponse).toList();
    }
}
