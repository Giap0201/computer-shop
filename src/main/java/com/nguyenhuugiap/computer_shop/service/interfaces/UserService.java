package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.request.UserCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.request.UserUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest userCreationRequest);
    UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest);
    List<UserResponse> getAllUsers();
    void deleteUser(Long id);
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    UserResponse getUserByPhone(String phone);
}
