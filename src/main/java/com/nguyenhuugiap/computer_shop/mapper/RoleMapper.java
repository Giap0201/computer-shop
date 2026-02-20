package com.nguyenhuugiap.computer_shop.mapper;

import com.nguyenhuugiap.computer_shop.dto.request.role.RoleRequest;
import com.nguyenhuugiap.computer_shop.dto.response.role.RoleResponse;
import com.nguyenhuugiap.computer_shop.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toRoleResponse(Role role);
    @Mapping(target = "id", ignore = true)
    Role toRole(RoleRequest roleRequest);
}
