package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.request.BrandCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.request.BrandUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.response.BrandResponse;
import com.nguyenhuugiap.computer_shop.entity.Brand;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "slug", ignore = true)
    Brand toEntity(BrandCreationRequest request);

    BrandResponse toResponse(Brand brand);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "slug", ignore = true)
    void toUpdate(@MappingTarget Brand brand, BrandUpdateRequest request);
}
