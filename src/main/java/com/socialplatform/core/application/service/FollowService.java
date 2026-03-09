package com.socialplatform.core.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialplatform.core.application.dto.response.FollowResponse;
import com.socialplatform.core.application.dto.response.FollowStatsResponse;

public interface FollowService {

    Page<FollowResponse> getFollowers(UUID userId, Pageable pageable);

    Page<FollowResponse> getFollowing(UUID userId, Pageable pageable);

    Page<FollowResponse> getFollowActionsByUser(UUID userId, Pageable pageable);

    List<UUID> getFollowingIds(UUID userId);

    void follow(UUID followerId, UUID followedId);

    void unfollow(UUID followerId, UUID followedId);

    FollowStatsResponse getStats(UUID userId);

    boolean isFollowing(UUID followerId, UUID followedId);
}
