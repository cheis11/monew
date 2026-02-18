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
public class CommentActivityDto {

    private UUID id;
    private UUID articleId;
    private String articleTitle;
    private UUID userId;
    private String userNickname;
    private String content;
    private long likeCount;
    private LocalDateTime createdAt;

    @Builder
    public CommentActivityDto(UUID id, UUID articleId, String articleTitle, UUID userId,
            String userNickname, String content, long likeCount, LocalDateTime createdAt) {
        this.id = id;
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.userId = userId;
        this.userNickname = userNickname;
        this.content = content;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }
}
