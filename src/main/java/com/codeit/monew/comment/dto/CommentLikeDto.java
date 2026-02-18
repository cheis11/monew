package com.codeit.monew.comment.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLikeDto {

    private UUID id;
    private UUID likedBy;
    private LocalDateTime createdAt;
    private UUID commentId;
    private UUID articleId;
    private UUID commentUserId;
    private String commentUserNickname;
    private String commentContent;
    private long commentLikeCount;
    private LocalDateTime commentCreatedAt;

    @Builder
    public CommentLikeDto(UUID id, UUID likedBy, LocalDateTime createdAt, UUID commentId,
            UUID articleId, UUID commentUserId, String commentUserNickname, String commentContent,
            long commentLikeCount, LocalDateTime commentCreatedAt) {
        this.id = id;
        this.likedBy = likedBy;
        this.createdAt = createdAt;
        this.commentId = commentId;
        this.articleId = articleId;
        this.commentUserId = commentUserId;
        this.commentUserNickname = commentUserNickname;
        this.commentContent = commentContent;
        this.commentLikeCount = commentLikeCount;
        this.commentCreatedAt = commentCreatedAt;
    }
}
