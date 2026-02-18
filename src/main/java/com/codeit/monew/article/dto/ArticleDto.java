package com.codeit.monew.article.dto;

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
public class ArticleDto {

    private UUID id;
    private String source;
    private String sourceUrl;
    private String title;
    private LocalDateTime publishDate;
    private String summary;
    private long commentCount;
    private long viewCount;
    private boolean viewedByMe;

    @Builder
    public ArticleDto(UUID id, String source, String sourceUrl, String title,
            LocalDateTime publishDate, String summary, long commentCount, long viewCount,
            boolean viewedByMe) {
        this.id = id;
        this.source = source;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.publishDate = publishDate;
        this.summary = summary;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.viewedByMe = viewedByMe;
    }
}
