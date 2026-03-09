package com.socialplatform.core.application.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.socialplatform.core.application.dto.request.PostRequest;
import com.socialplatform.core.application.dto.response.PostResponse;
import com.socialplatform.core.application.event.ContentDeletedEvent;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.exception.UnauthorizedOperationException;
import com.socialplatform.core.application.mapper.PostMapper;
import com.socialplatform.core.application.service.CommentService;
import com.socialplatform.core.application.service.FollowService;
import com.socialplatform.core.application.service.PostService;
import com.socialplatform.core.application.service.ReactionService;
import com.socialplatform.core.domain.enums.AccountVisibility;
import com.socialplatform.core.domain.enums.PostVisibility;
import com.socialplatform.core.domain.model.Post;
import com.socialplatform.core.domain.model.Role;
import com.socialplatform.core.domain.repository.PostRepository;
import com.socialplatform.core.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final ReactionService reactionService;
    private final FollowService followService;
    private final PostMapper postMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public PostResponse create(PostRequest request, UUID authorId) {
        var post = Post.builder()
                .author(userRepository.getReferenceById(authorId))
                .content(request.content())
                .imageUrl(request.imageUrl())
                .postVisibility(request.postVisibility())
                .build();

        return postMapper.toResponse(postRepository.save(post));
    }

    @Override
    public PostResponse getById(UUID id) {
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (post.isDeleted()) {
            if (!hasAdminAuthority()) {
                throw new ResourceNotFoundException("Post not found");
            }
        }

        return postMapper.toResponse(post);
    }

    @Override
    @Transactional
    public PostResponse update(UUID id, PostRequest request, UUID authorId) {
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (post.isDeleted()) {
            throw new ResourceNotFoundException("Post not found");
        }

        if (!post.getAuthor().getId().equals(authorId)) {
            throw new UnauthorizedOperationException("You are not the owner of this post");
        }

        post.updateContent(request.content());
        post.setImageUrl(request.imageUrl());
        post.setPostVisibility(request.postVisibility());

        return postMapper.toResponse(postRepository.save(post));
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID authorId) {
        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (post.isDeleted()) {
            throw new ResourceNotFoundException("Post not found");
        }

        if (!post.getAuthor().getId().equals(authorId)) {
            throw new UnauthorizedOperationException("Permission denied to delete this post");
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        postRepository.softDeleteById(id, now);
        commentService.softDeleteByPostId(id);
        reactionService.softDeleteByPost(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void softDeleteWithRelatedContent(UUID postId, UUID adminId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        UUID postAuthorId = post.getAuthor().getId();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        postRepository.softDeleteById(postId, now);
        commentService.softDeleteByPostId(postId);
        reactionService.softDeleteByPost(postId);

        eventPublisher.publishEvent(new ContentDeletedEvent(
                postId,
                postAuthorId,
                adminId,
                "Content moderation - violated community guidelines"));
    }

    @Override
    public Page<PostResponse> getFeed(UUID currentUserId, Pageable pageable) {
        List<UUID> followedIds = followService.getFollowingIds(currentUserId);

        return postRepository
                .findPersonalFeed(followedIds, currentUserId, PostVisibility.PUBLIC, AccountVisibility.PUBLIC, pageable)
                .map(postMapper::toResponse);
    }

    @Override
    public Page<PostResponse> getUserPosts(UUID userId, UUID currentUserId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        if (userId.equals(currentUserId)) {
            return postRepository.findByAuthorIdOrderByCreatedAtDesc(userId, pageable)
                    .map(postMapper::toResponse);
        }

        return postRepository.findPublicPostsByUser(userId, PostVisibility.PUBLIC, AccountVisibility.PUBLIC, pageable)
                .map(postMapper::toResponse);
    }

    private boolean hasAdminAuthority() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> ("ROLE_" + Role.ADMIN).equals(a.getAuthority())
                        || ("ROLE_" + Role.SUPERADMIN).equals(a.getAuthority()));
    }
}
