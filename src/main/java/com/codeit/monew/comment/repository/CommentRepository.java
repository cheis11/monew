package com.codeit.monew.comment.repository;

import com.codeit.monew.comment.entity.Comment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, UUID>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Comment> {
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true, flushAutomatically = true)
    @org.springframework.data.jpa.repository.Query(value = "DELETE FROM comment WHERE id = :commentId", nativeQuery = true)
    void hardDeleteById(@org.springframework.data.repository.query.Param("commentId") UUID commentId);

    @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM comment WHERE id = :commentId", nativeQuery = true)
    long countByIdIncludingDeleted(@org.springframework.data.repository.query.Param("commentId") UUID commentId);
}
