package com.codeit.monew.notification.dto;

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
public class NotificationDto {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean confirmed;
    private UUID userId;
    private String content;
    private String resourceType;
    private UUID resourceId;

    @Builder
    public NotificationDto(UUID id, LocalDateTime createdAt, LocalDateTime updatedAt,
            boolean confirmed, UUID userId, String content, String resourceType, UUID resourceId) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.confirmed = confirmed;
        this.userId = userId;
        this.content = content;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
}
