package com.socialplatform.core.application.service;

import com.socialplatform.core.application.dto.request.LoginRequest;
import com.socialplatform.core.application.dto.request.RegisterRequest;
import com.socialplatform.core.application.dto.response.AuthResponse;
import com.socialplatform.core.application.dto.response.UserResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refreshAccessToken(String refreshToken);

    void revokeRefreshToken(String refreshToken);

    UserResponse register(RegisterRequest request);

    UserResponse registerAdmin(RegisterRequest request);
}
