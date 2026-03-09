package com.socialplatform.core.application.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialplatform.core.application.dto.request.CommentRequest;
import com.socialplatform.core.application.dto.response.CommentResponse;
import com.socialplatform.core.application.event.CommentCreatedEvent;
import com.socialplatform.core.application.exception.DeletedPostException;
import com.socialplatform.core.application.exception.ResourceNotFoundException;
import com.socialplatform.core.application.exception.UnauthorizedOperationException;
import com.socialplatform.core.application.mapper.CommentMapper;
import com.socialplatform.core.application.service.CommentService;
import com.socialplatform.core.domain.model.Comment;
import com.socialplatform.core.domain.repository.CommentRepository;
import com.socialplatform.core.domain.repository.PostRepository;
import com.socialplatform.core.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public CommentResponse getById(UUID id) {
        return commentRepository.findActiveById(id)
                .map(commentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + id));
    }

    @Override
    public Page<CommentResponse> getByPostId(UUID postId, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found");
        }
        return commentRepository.findByPostId(postId, pageable)
                .map(commentMapper::toResponse);
    }

    @Override
    public Page<CommentResponse> getByUserId(UUID userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return commentRepository.findByUserId(userId, pageable)
                .map(commentMapper::toResponse);
    }

    @Override
    @Transactional
    public CommentResponse create(UUID postId, CommentRequest request, UUID authorId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (post.isDeleted()) {
            throw new DeletedPostException("Cannot comment on a deleted post");
        }

        var comment = Comment.builder()
                .content(request.content())
                .post(post)
                .author(userRepository.getReferenceById(authorId))
                .build();

        var saved = commentRepository.save(comment);

        eventPublisher.publishEvent(new CommentCreatedEvent(
                saved.getId(),
                post.getId(),
                authorId,
                post.getAuthor().getId()));

        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CommentResponse update(UUID id, CommentRequest request, UUID authorId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new UnauthorizedOperationException("You are not the owner of this comment");
        }

        comment.updateContent(request.content());
        return commentMapper.toResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(UUID id, UUID authorId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new UnauthorizedOperationException("Permission denied to delete this comment");
        }

        comment.delete();
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void softDeleteByPostId(UUID postId) {
        commentRepository.softDeleteByPostId(postId, LocalDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public long countByPostId(UUID postId) {
        return commentRepository.countByPostId(postId);
    }

    @Override
    public long countByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return commentRepository.countByUserId(userId);
    }
}
