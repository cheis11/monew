package com.codeit.monew.comment.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CursorPageResponseCommentDto {

    private List<CommentDto> content;
    private String nextCursor;
    private LocalDateTime nextAfter;
    private int size;
    private long totalElements;
    private boolean hasNext;

    @Builder
    public CursorPageResponseCommentDto(List<CommentDto> content, String nextCursor,
            LocalDateTime nextAfter, int size, long totalElements, boolean hasNext) {
        this.content = content;
        this.nextCursor = nextCursor;
        this.nextAfter = nextAfter;
        this.size = size;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
    }
}
