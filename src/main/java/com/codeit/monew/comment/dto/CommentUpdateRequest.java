package com.codeit.monew.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentUpdateRequest {

    @NotBlank
    @Size(min = 1, max = 500)
    private String content;

    public CommentUpdateRequest(String content) {
        this.content = content;
    }
}
