package com.socialplatform.core.application.service.impl;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import com.socialplatform.core.application.dto.request.LoginRequest;
import com.socialplatform.core.application.dto.request.RegisterRequest;
import com.socialplatform.core.application.dto.response.AuthResponse;
import com.socialplatform.core.application.dto.response.UserResponse;
import com.socialplatform.core.application.exception.AccountDisabledException;
import com.socialplatform.core.application.exception.EmailAlreadyInUseException;
import com.socialplatform.core.application.exception.InvalidCredentialsException;
import com.socialplatform.core.application.exception.InvalidTokenException;
import com.socialplatform.core.application.exception.MinimumAgeRequiredException;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.exception.UsernameAlreadyExistsException;
import com.socialplatform.core.application.mapper.UserMapper;
import com.socialplatform.core.application.provider.JwtTokenProvider;
import com.socialplatform.core.application.service.AuthService;
import com.socialplatform.core.application.service.RefreshTokenService;
import com.socialplatform.core.domain.enums.UserStatus;
import com.socialplatform.core.domain.model.Profile;
import com.socialplatform.core.domain.model.Role;
import com.socialplatform.core.domain.model.User;
import com.socialplatform.core.domain.repository.ProfileRepository;
import com.socialplatform.core.domain.repository.RoleRepository;
import com.socialplatform.core.domain.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtDecoder jwtDecoder;
    private final RefreshTokenService refreshTokenService;

    @Override
    public AuthResponse login(LoginRequest request) {

        var user = userRepository.findByEmail(request.identifier())
                .or(() -> userRepository.findByUsername(request.identifier()))
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        if (user.isDeleted()) {
            throw new AccountDisabledException("Account has been deleted");
        }

        if (user.getStatus() == UserStatus.BANNED) {
            throw new AccountDisabledException("Account has been banned");
        }

        var accessToken = jwtTokenProvider.generateAccessToken(user);
        var expiresIn = jwtTokenProvider.getAccessTokenValidity();
        var userResponse = userMapper.toResponse(user);

        String refreshJti = UUID.randomUUID().toString();
        String refreshToken = jwtTokenProvider.generateRefreshToken(user, refreshJti);
        refreshTokenService.registerNewToken(user, refreshToken, refreshJti,
                jwtTokenProvider.getRefreshTokenExpiryInstant());

        return new AuthResponse(userResponse, accessToken, refreshToken, expiresIn);
    }

    @Override
    @Transactional
    public AuthResponse refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidTokenException("Refresh token is missing");
        }

        try {
            Jwt jwt = jwtDecoder.decode(refreshToken);
            String userId = jwt.getSubject();
            String tokenType = jwt.getClaimAsString("type");
            String jti = jwt.getClaimAsString("jti");

            if (!"refresh".equals(tokenType)) {
                throw new InvalidTokenException("Invalid token type");
            }

            if (jti == null || jti.isBlank()) {
                throw new InvalidTokenException("Refresh token missing jti");
            }

            User user = userRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (user.isDeleted() || user.getStatus() != UserStatus.ACTIVE) {
                throw new InvalidTokenException("User account is not active");
            }

            refreshTokenService.validateAndRotate(refreshToken, jti, user.getId());

            String newAccessToken = jwtTokenProvider.generateAccessToken(user);
            String newRefreshJti = UUID.randomUUID().toString();
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(user, newRefreshJti);
            refreshTokenService.registerNewToken(user, newRefreshToken, newRefreshJti,
                    jwtTokenProvider.getRefreshTokenExpiryInstant());
            long expiresIn = jwtTokenProvider.getAccessTokenValidity();

            return new AuthResponse(null, newAccessToken, newRefreshToken, expiresIn);

        } catch (InvalidTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {
        Optional.ofNullable(refreshToken)
                .filter(token -> !token.isBlank())
                .ifPresent(refreshTokenService::revokeByRawToken);
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        validateRegistration(request);

        var defaultRole = getDefaultRole();
        var user = createUser(request, defaultRole);
        var savedUser = userRepository.save(user);

        createUserProfile(savedUser, request);

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse registerAdmin(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException("Email already registered: " + request.email());
        }

        var adminRole = roleRepository.findByNameIgnoreCase(Role.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role ADMIN not found"));

        var user = createUser(request, adminRole);
        var savedUser = userRepository.save(user);

        createUserProfile(savedUser, request);

        return userMapper.toResponse(savedUser);
    }

    private void validateRegistration(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyInUseException("Email already registered: " + request.email());
        }

        validateMinimumAge(request.birthDate());
    }

    private Role getDefaultRole() {
        return roleRepository.findByDefaultRoleTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Default role not configured in system"));
    }

    private User createUser(RegisterRequest request, Role role) {
        return User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .build();
    }

    private void createUserProfile(User user, RegisterRequest request) {
        var profile = Profile.builder()
                .user(user)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(request.birthDate())
                .build();

        profileRepository.save(profile);
    }

    private void validateMinimumAge(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 13) {
            throw new MinimumAgeRequiredException("User must be at least 13 years old to register");
        }
    }

}
