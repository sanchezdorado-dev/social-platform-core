package com.socialplatform.core.domain.model;

import org.hibernate.annotations.SQLRestriction;

import com.socialplatform.core.domain.enums.ReactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "reactions", indexes = {
        @Index(name = "idx_reactions_post_type", columnList = "post_id, type"),
        @Index(name = "idx_reactions_user", columnList = "user_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_reactions_user_post", columnNames = { "user_id", "post_id" })
})
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class Reaction extends BaseEntity {

    @Setter(AccessLevel.PROTECTED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    private Post post;

    public void changeType(ReactionType newType) {
        if (newType == null)
            throw new IllegalArgumentException("Type cannot be null");
        this.type = newType;
    }
}
