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
public class CommentDto {

    private UUID id;
    private UUID articleId;
    private UUID userId;
    private String userNickname;
    private String content;
    private long likeCount;
    private boolean likedByMe;
    private LocalDateTime createdAt;

    @Builder
    public CommentDto(UUID id, UUID articleId, UUID userId, String userNickname, String content,
            long likeCount, boolean likedByMe, LocalDateTime createdAt) {
        this.id = id;
        this.articleId = articleId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.content = content;
        this.likeCount = likeCount;
        this.likedByMe = likedByMe;
        this.createdAt = createdAt;
    }
}
