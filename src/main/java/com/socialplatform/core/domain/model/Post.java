package com.socialplatform.core.domain.model;

import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

import com.socialplatform.core.domain.enums.PostVisibility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "posts", indexes = {
        @Index(name = "idx_posts_visibility_date", columnList = "visibility_type, created_at"),
        @Index(name = "idx_posts_author_date", columnList = "author_id, created_at")
})
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class Post extends BaseEntity {

    @Setter(AccessLevel.PROTECTED)
    @NotBlank
    @Size(max = 1500)
    @Column(nullable = false, length = 1500)
    private String content;

    @Setter
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility_type", nullable = false)
    private PostVisibility postVisibility = PostVisibility.PUBLIC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private User author;

    public void updateContent(String newContent) {
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("Post content cannot be empty or blank");
        }

        if (newContent.length() > 1500) {
            throw new IllegalArgumentException("Post content exceeds maximum length of 1500 characters");
        }

        this.content = newContent.strip();
    }

    public boolean isPublic() {
        return PostVisibility.PUBLIC.equals(this.postVisibility);
    }

    public boolean isOwnedBy(UUID userId) {
        if (userId == null || this.author == null)
            return false;

        return this.author.getId().equals(userId);
    }
}
