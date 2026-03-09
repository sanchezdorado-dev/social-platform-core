package com.socialplatform.core.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialplatform.core.application.dto.request.ProfileRequest;
import com.socialplatform.core.application.dto.response.ProfileResponse;

public interface ProfileService {

    Page<ProfileResponse> searchByName(String query, Pageable pageable);

    ProfileResponse getByUserId(UUID userId);

    ProfileResponse getByUsername(String username);

    ProfileResponse update(UUID userId, ProfileRequest request);

}
