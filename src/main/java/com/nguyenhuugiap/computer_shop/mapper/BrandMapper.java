package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.request.BrandCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.request.BrandUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.response.BrandResponse;
import com.nguyenhuugiap.computer_shop.entity.Brand;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Mapper(componentModel = "spring")
public abstract class BrandMapper {
    @Value("${storage.location}")
    String folderName;

    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "products", ignore = true)
    @Mapping(target = "slug", ignore = true)
    public abstract Brand toEntity(BrandCreationRequest request);

    @Mapping(target = "logoUrl", ignore = true)
    public abstract BrandResponse toResponse(Brand brand);

    @AfterMapping
    protected void mapperLogoUrl(@MappingTarget BrandResponse.BrandResponseBuilder brandResponse, Brand brand) {
        if (brand.getLogo() == null || brand.getLogo().isEmpty()) return;
        String baseUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .build()
                .toUriString();
        brandResponse.logoUrl(baseUrl + "/" + folderName + "/" + brand.getLogo());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "slug", ignore = true)
    public abstract void toUpdate(@MappingTarget Brand brand, BrandUpdateRequest request);
}
