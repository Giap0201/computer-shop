package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.request.BrandRequest;
import com.nguyenhuugiap.computer_shop.dto.response.BrandResponse;
import com.nguyenhuugiap.computer_shop.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toEntity(BrandRequest request);

    BrandResponse toResponse(Brand brand);
}
