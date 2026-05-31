package com.nguyenhuugiap.computer_shop.service.impl;


import com.nguyenhuugiap.computer_shop.dto.user.RoleAssignmentRequest;
import com.nguyenhuugiap.computer_shop.dto.user.UserCreationRequest;
import com.nguyenhuugiap.computer_shop.dto.user.UserUpdateRequest;
import com.nguyenhuugiap.computer_shop.dto.user.UserResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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
        Role roleUser = roleRepository.findByName(RoleType.USER.name()).orElseThrow(() ->
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
    @PostAuthorize("returnObject.id.toString() == authentication.name")
    public UserResponse getUserById(Long id) {
        log.info("Get User by id: {}", id);
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
        User use = userRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toResponse(use);
    }

    @Override
    public void assignRoleToUser(Long userId, RoleAssignmentRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepository.findByName(request.getRoleName()).orElseThrow(() ->
                new AppException(ErrorCode.ROLE_NOT_FOUND));


        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public void revokeRoleFromUser(Long userId, RoleAssignmentRequest request) {
        String currentLoggedInUser = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_FOUND));

        if (userId.toString().equals(currentLoggedInUser)) {
            throw new AppException(ErrorCode.CANNOT_SELF_REVOKE_ROLE);
        }
        Role role = roleRepository.findByName(request.getRoleName()).orElseThrow(() ->
                new AppException(ErrorCode.ROLE_NOT_FOUND));

        user.getRoles().remove(role);
        userRepository.save(user);
    }
}
