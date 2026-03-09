package com.socialplatform.core.application.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialplatform.core.application.dto.request.ProfileRequest;
import com.socialplatform.core.application.dto.response.ProfileResponse;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.mapper.ProfileMapper;
import com.socialplatform.core.application.service.ProfileService;
import com.socialplatform.core.domain.enums.AccountVisibility;
import com.socialplatform.core.domain.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Override
    public ProfileResponse getByUserId(UUID userId) {
        return profileRepository.findByUserId(userId)
                .map(profileMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user ID: " + userId));
    }

    @Override
    public ProfileResponse getByUsername(String username) {
        return profileRepository.findByUserUsernameIgnoreCase(username)
                .map(profileMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for username: " + username));
    }

    @Override
    @Transactional
    public ProfileResponse update(UUID userId, ProfileRequest request) {
        var profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user ID: " + userId));

        profileMapper.updateEntityFromRequest(request, profile);

        if (profile.getUser().getRole().isAdmin()) {
            profile.setAccountVisibility(AccountVisibility.PRIVATE);
        }

        var updatedProfile = profileRepository.save(profile);

        return profileMapper.toResponse(updatedProfile);
    }

    @Override
    public Page<ProfileResponse> searchByName(String query, Pageable pageable) {

        return profileRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                query, query, pageable)
                .map(profileMapper::toResponse);
    }
}
