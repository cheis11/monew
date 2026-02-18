package com.codeit.monew.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRegisterRequest {

    @NotNull
    private UUID articleId;

    @NotNull
    private UUID userId;

    @NotBlank
    @Size(min = 1, max = 500)
    private String content;

    public CommentRegisterRequest(UUID articleId, UUID userId, String content) {
        this.articleId = articleId;
        this.userId = userId;
        this.content = content;
    }
}
