package com.codeit.monew.interest.dto;

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
public class InterestDto {

    private UUID id;
    private String name;
    private List<String> keywords;
    private long subscriberCount;
    private boolean subscribedByMe;

    @Builder
    public InterestDto(UUID id, String name, List<String> keywords, long subscriberCount,
            boolean subscribedByMe) {
        this.id = id;
        this.name = name;
        this.keywords = keywords;
        this.subscriberCount = subscriberCount;
        this.subscribedByMe = subscribedByMe;
    }
}
