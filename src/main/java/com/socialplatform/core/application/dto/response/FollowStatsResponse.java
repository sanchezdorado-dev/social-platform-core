package com.socialplatform.core.application.dto.response;

public record FollowStatsResponse(
        long followersCount,
        long followingCount) {
}
