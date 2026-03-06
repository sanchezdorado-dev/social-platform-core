package com.socialplatform.core.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socialplatform.core.domain.model.Follow;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Follow f WHERE f.follower.id = :followerId AND f.followed.id = :followedId")
    void deleteByFollowerIdAndFollowedId(@Param("followerId") UUID followerId, @Param("followedId") UUID followedId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Follow f WHERE f.follower.id = :userId OR f.followed.id = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT f FROM Follow f WHERE f.follower.id = :userId ORDER BY f.createdAt DESC")
    Page<Follow> findByFollowerId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT f.followed.id FROM Follow f WHERE f.follower.id = :userId")
    List<UUID> findFollowedIdsByFollowerId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followed.id = :userId")
    long countFollowers(@Param("userId") UUID userId);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId")
    long countFollowed(@Param("userId") UUID userId);

    Page<Follow> findByFollowedId(UUID userId, Pageable pageable);

    boolean existsByFollowerIdAndFollowedId(UUID followerId, UUID followedId);
}
