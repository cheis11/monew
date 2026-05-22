package com.codeit.monew.comment.repository;

import com.codeit.monew.comment.entity.CommentLike;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {
    boolean existsByCommentIdAndUserId(UUID commentId, UUID userId);
    
    java.util.Optional<CommentLike> findByCommentIdAndUserId(UUID commentId, UUID userId);

    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    @org.springframework.data.jpa.repository.Query(value = "DELETE FROM comment_like WHERE comment_id = :commentId", nativeQuery = true)
    void deleteByCommentId(@org.springframework.data.repository.query.Param("commentId") UUID commentId);
}
