package com.codeit.monew.notification.dto;

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
public class CursorPageResponseNotificationDto {

    private List<NotificationDto> content;
    private String nextCursor;
    private LocalDateTime nextAfter;
    private int size;
    private long totalElements;
    private boolean hasNext;

    @Builder
    public CursorPageResponseNotificationDto(List<NotificationDto> content, String nextCursor,
            LocalDateTime nextAfter, int size, long totalElements, boolean hasNext) {
        this.content = content;
        this.nextCursor = nextCursor;
        this.nextAfter = nextAfter;
        this.size = size;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
    }
}
