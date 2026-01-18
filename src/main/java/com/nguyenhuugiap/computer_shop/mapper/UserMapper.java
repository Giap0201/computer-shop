package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.request.UserCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.response.UserResponse;
import com.nguyenhuugiap.computer_shop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserCreationRequest request);

    UserResponse toResponse(User user);
}
