package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.request.category.CategoryCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.request.category.CategoryUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.response.CategoryResponse;
import com.nguyenhuugiap.computer_shop.entity.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "status", ignore = true)
//    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryCreationRequest categoryRequest);
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "parent.name", target = "parentName")
    CategoryResponse toResponse(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategory(@MappingTarget Category category, CategoryUpdateRequest categoryUpdateRequest);
}
