package com.codeit.monew.comment.repository;

import com.codeit.monew.comment.entity.Comment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, UUID>, JpaSpecificationExecutor<Comment> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM comment WHERE id = :commentId", nativeQuery = true)
    void hardDeleteById(@Param("commentId") UUID commentId);

    @Query(value = "SELECT COUNT(*) FROM comment WHERE id = :commentId", nativeQuery = true)
    long countByIdIncludingDeleted(@Param("commentId") UUID commentId);

    java.util.List<Comment> findByUserId(UUID userId);
    long countByArticleId(UUID articleId);
}
