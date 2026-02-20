package com.nguyenhuugiap.computer_shop.service.impl;

import com.nguyenhuugiap.computer_shop.dto.request.category.CategoryCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.request.category.CategoryUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.response.CategoryResponse;
import com.nguyenhuugiap.computer_shop.entity.Category;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.mapper.CategoryMapper;
import com.nguyenhuugiap.computer_shop.repository.CategoryRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreationRequest request) {
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

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryRoots() {
        return categoryRepository.findAllRoots()
                .stream().map(categoryMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategoryById(long id) {
        if (!categoryRepository.existsById(id))
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        if (categoryRepository.existsByParentId(id))
            throw new AppException(ErrorCode.CANNOT_DELETE_HAS_CHILDREN);
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        System.out.println("DEBUG NAME: " + request.getName());
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (request.getName() != null && categoryRepository.existsByNameAndIdNot(request.getName(), id))
            throw new AppException(ErrorCode.CATEGORY_EXISTS);
        if (request.getParentId() != null) {
            if (request.getParentId().equals(category.getId()))
                throw new AppException(ErrorCode.CANNOT_UPDATE_CATEGORY);
            Category newParent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            if (isDescendant(category, newParent)) throw new AppException(ErrorCode.CANNOT_UPDATE_CATEGORY);
            category.setParent(newParent);
        }
        categoryMapper.updateCategory(category, request);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    private boolean isDescendant(Category source, Category target) {
        Category current = target;
        while (current != null) {
            if (current.getId().equals(source.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
}

