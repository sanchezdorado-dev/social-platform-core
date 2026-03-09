package com.socialplatform.core.application.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialplatform.core.application.dto.response.FollowResponse;
import com.socialplatform.core.application.dto.response.FollowStatsResponse;
import com.socialplatform.core.application.event.NewFollowerEvent;
import com.socialplatform.core.application.exception.DuplicateResourceException;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.exception.SelfFollowException;
import com.socialplatform.core.application.mapper.FollowMapper;
import com.socialplatform.core.application.service.FollowService;
import com.socialplatform.core.domain.model.Follow;
import com.socialplatform.core.domain.repository.FollowRepository;
import com.socialplatform.core.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FollowMapper followMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void follow(UUID followerId, UUID followedId) {
        if (followerId.equals(followedId)) {
            throw new SelfFollowException("You cannot follow yourself");
        }

        validateUserExists(followerId);
        validateUserExists(followedId);

        if (followRepository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            throw new DuplicateResourceException("Already following this user");
        }

        var newFollow = Follow.builder()
                .follower(userRepository.getReferenceById(followerId))
                .followed(userRepository.getReferenceById(followedId))
                .build();
        followRepository.save(newFollow);

        eventPublisher.publishEvent(new NewFollowerEvent(followerId, followedId));
    }

    @Override
    @Transactional
    public void unfollow(UUID followerId, UUID followedId) {
        if (!followRepository.existsByFollowerIdAndFollowedId(followerId, followedId)) {
            throw new ResourceNotFoundException("Follow relationship not found");
        }

        followRepository.deleteByFollowerIdAndFollowedId(followerId, followedId);
    }

    @Override
    public Page<FollowResponse> getFollowers(UUID userId, Pageable pageable) {
        validateUserExists(userId);
        return followRepository.findByFollowedId(userId, pageable)
                .map(followMapper::toFollowerResponse);
    }

    @Override
    public Page<FollowResponse> getFollowing(UUID userId, Pageable pageable) {
        validateUserExists(userId);
        return followRepository.findByFollowerId(userId, pageable)
                .map(followMapper::toFollowingResponse);
    }

    @Override
    public Page<FollowResponse> getFollowActionsByUser(UUID userId, Pageable pageable) {
        return getFollowing(userId, pageable);
    }

    @Override
    public FollowStatsResponse getStats(UUID userId) {
        validateUserExists(userId);

        long followersCount = followRepository.countFollowers(userId);
        long followingCount = followRepository.countFollowed(userId);

        return new FollowStatsResponse(followersCount, followingCount);
    }

    @Override
    public List<UUID> getFollowingIds(UUID userId) {
        return followRepository.findFollowedIdsByFollowerId(userId);
    }

    @Override
    public boolean isFollowing(UUID followerId, UUID followedId) {
        return followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }

    private void validateUserExists(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
    }
}
