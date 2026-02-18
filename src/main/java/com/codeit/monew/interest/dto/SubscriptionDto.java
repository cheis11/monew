package com.codeit.monew.interest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionDto {

    private UUID id;
    private UUID interestId;
    private String interestName;
    private List<String> interestKeywords;
    private long interestSubscriberCount;
    private LocalDateTime createdAt;

    @Builder
    public SubscriptionDto(UUID id, UUID interestId, String interestName,
            List<String> interestKeywords, long interestSubscriberCount, LocalDateTime createdAt) {
        this.id = id;
        this.interestId = interestId;
        this.interestName = interestName;
        this.interestKeywords = interestKeywords;
        this.interestSubscriberCount = interestSubscriberCount;
        this.createdAt = createdAt;
    }
}
