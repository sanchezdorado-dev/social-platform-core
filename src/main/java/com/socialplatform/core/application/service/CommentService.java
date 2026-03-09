package com.socialplatform.core.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialplatform.core.application.dto.request.CommentRequest;
import com.socialplatform.core.application.dto.response.CommentResponse;

public interface CommentService {

    Page<CommentResponse> getByPostId(UUID postId, Pageable pageable);

    Page<CommentResponse> getByUserId(UUID userId, Pageable pageable);

    CommentResponse getById(UUID id);

    CommentResponse create(UUID postId, CommentRequest request, UUID authorId);

    CommentResponse update(UUID id, CommentRequest request, UUID authorId);

    void delete(UUID id, UUID authorId);

    void softDeleteByPostId(UUID postId);

    long countByPostId(UUID postId);

    long countByUserId(UUID userId);
}
