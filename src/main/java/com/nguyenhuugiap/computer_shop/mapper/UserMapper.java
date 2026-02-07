package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.request.UserCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.request.UserUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.response.UserResponse;
import com.nguyenhuugiap.computer_shop.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "gender", source = "gender")
    User toEntity(UserCreationRequest request);

    UserResponse toResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "passwordHash", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
