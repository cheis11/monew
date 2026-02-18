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
public class CommentLikeActivityDto {

    private UUID id;
    private LocalDateTime createdAt;
    private UUID commentId;
    private UUID articleId;
    private String articleTitle;
    private UUID commentUserId;
    private String commentUserNickname;
    private String commentContent;
    private long commentLikeCount;
    private LocalDateTime commentCreatedAt;

    @Builder
    public CommentLikeActivityDto(UUID id, LocalDateTime createdAt, UUID commentId, UUID articleId,
            String articleTitle, UUID commentUserId, String commentUserNickname, String commentContent,
            long commentLikeCount, LocalDateTime commentCreatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.commentId = commentId;
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.commentUserId = commentUserId;
        this.commentUserNickname = commentUserNickname;
        this.commentContent = commentContent;
        this.commentLikeCount = commentLikeCount;
        this.commentCreatedAt = commentCreatedAt;
    }
}
