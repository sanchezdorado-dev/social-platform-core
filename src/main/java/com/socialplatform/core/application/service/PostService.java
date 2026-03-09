package com.socialplatform.core.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialplatform.core.application.dto.request.PostRequest;
import com.socialplatform.core.application.dto.response.PostResponse;

public interface PostService {

    Page<PostResponse> getFeed(UUID currentUserId, Pageable pageable);

    Page<PostResponse> getUserPosts(UUID userId, UUID currentUserId, Pageable pageable);

    PostResponse create(PostRequest request, UUID authorId);

    PostResponse getById(UUID id);

    PostResponse update(UUID id, PostRequest request, UUID authorId);

    void delete(UUID id, UUID authorId);

    void softDeleteWithRelatedContent(UUID postId, UUID adminId);

}
