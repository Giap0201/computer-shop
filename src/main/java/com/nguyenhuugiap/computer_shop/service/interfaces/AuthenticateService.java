package com.nguyenhuugiap.computer_shop.service.interfaces;

import com.nguyenhuugiap.computer_shop.dto.request.AuthenticateRequest;
import com.nguyenhuugiap.computer_shop.dto.response.AuthenticateResponse;
import com.nguyenhuugiap.computer_shop.entity.User;

public interface AuthenticateService {
    String generateToken(User user);
    AuthenticateResponse authenticate(AuthenticateRequest request);
}
