package com.nguyenhuugiap.computer_shop.service.impl;


import com.nguyenhuugiap.computer_shop.dto.request.UserCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.request.UserUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.response.UserResponse;
import com.nguyenhuugiap.computer_shop.entity.Role;
import com.nguyenhuugiap.computer_shop.entity.User;
import com.nguyenhuugiap.computer_shop.enums.RoleType;
import com.nguyenhuugiap.computer_shop.exception.AppException;
import com.nguyenhuugiap.computer_shop.exception.ErrorCode;
import com.nguyenhuugiap.computer_shop.mapper.UserMapper;
import com.nguyenhuugiap.computer_shop.repository.RoleRepository;
import com.nguyenhuugiap.computer_shop.repository.UserRepository;
import com.nguyenhuugiap.computer_shop.service.interfaces.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    @Override
    public UserResponse createUser(UserCreationRequest userCreationRequest) {
        if (userRepository.existsByEmail(userCreationRequest.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTS);
        if (userRepository.existsByPhone(userCreationRequest.getPhone()))
            throw new AppException(ErrorCode.USER_EXISTS);
        User user = userMapper.toEntity(userCreationRequest);
        user.setPasswordHash(passwordEncoder.encode(userCreationRequest.getPassword()));
        Role roleUser = roleRepository.findByName(RoleType.USER.name()).orElseThrow(()->
                new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.getRoles().add(roleUser);
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        if (!userRepository.existsById(id))
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (userUpdateRequest.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse).toList();
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id))
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByPhone(String phone) {
        User user = userRepository.findByPhone(phone).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        Long id = Long.valueOf(context.getAuthentication().getName());
        User use = userRepository.findById(id).orElseThrow(()->
                new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(use);
    }
}
