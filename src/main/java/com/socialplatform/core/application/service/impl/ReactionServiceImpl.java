package com.socialplatform.core.application.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialplatform.core.application.dto.request.ReactionRequest;
import com.socialplatform.core.application.dto.response.ReactionResponse;
import com.socialplatform.core.application.event.ReactionCreatedEvent;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.mapper.ReactionMapper;
import com.socialplatform.core.application.service.ReactionService;
import com.socialplatform.core.domain.model.Reaction;
import com.socialplatform.core.domain.repository.PostRepository;
import com.socialplatform.core.domain.repository.ReactionRepository;
import com.socialplatform.core.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReactionMapper reactionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Optional<ReactionResponse> toggle(UUID postId, ReactionRequest request, UUID userId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        var userProxy = userRepository.getReferenceById(userId);
        var existing = reactionRepository.findByUserIdAndPostId(userId, postId);

        if (existing.isPresent()) {
            var existingReaction = existing.get();

            if (existingReaction.getType().equals(request.type())) {
                reactionRepository.delete(existingReaction);
                return Optional.empty();
            }

            existingReaction.changeType(request.type());
            var updated = reactionRepository.save(existingReaction);

            eventPublisher.publishEvent(new ReactionCreatedEvent(
                    updated.getId(),
                    postId,
                    userId,
                    post.getAuthor().getId(),
                    request.type().toString()));

            return Optional.of(reactionMapper.toResponse(updated));
        }

        var newReaction = Reaction.builder()
                .post(post)
                .user(userProxy)
                .type(request.type())
                .build();

        var saved = reactionRepository.save(newReaction);

        eventPublisher.publishEvent(new ReactionCreatedEvent(
                saved.getId(),
                postId,
                userId,
                post.getAuthor().getId(),
                request.type().toString()));

        return Optional.of(reactionMapper.toResponse(saved));
    }

    @Override
    public Map<String, Long> getSummaryByPost(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found");
        }

        var results = reactionRepository.countReactionsByPostIdGroupedByType(postId);

        return results.stream()
                .collect(Collectors.toMap(
                        result -> result.getReactionType(),
                        result -> result.getCount()));
    }

    @Override
    public Page<ReactionResponse> getByPost(UUID postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found");
        }
        return reactionRepository.findByPostId(postId, pageable)
                .map(reactionMapper::toResponse);
    }

    @Override
    public Page<ReactionResponse> getByUser(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return reactionRepository.findByUserId(userId, pageable)
                .map(reactionMapper::toResponse);
    }

    @Override
    public long countByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return reactionRepository.countByUserId(userId);
    }

    @Override
    @Transactional
    public void softDeleteByPost(UUID postId) {
        reactionRepository.softDeleteByPostId(postId, LocalDateTime.now(ZoneOffset.UTC));
    }
}
