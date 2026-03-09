package com.socialplatform.core.application.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialplatform.core.application.dto.request.ReactionRequest;
import com.socialplatform.core.application.dto.response.ReactionResponse;

public interface ReactionService {

    Page<ReactionResponse> getByPost(UUID postId, Pageable pageable);

    Page<ReactionResponse> getByUser(UUID userId, Pageable pageable);

    Map<String, Long> getSummaryByPost(UUID postId);

    Optional<ReactionResponse> toggle(UUID postId, ReactionRequest request, UUID userId);

    long countByUserId(UUID userId);

    void softDeleteByPost(UUID postId);
}
