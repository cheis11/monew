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
public class ArticleViewDto {

    private UUID id;
    private UUID viewedBy;
    private LocalDateTime createdAt;
    private UUID articleId;
    private String source;
    private String sourceUrl;
    private String articleTitle;
    private LocalDateTime articlePublishedDate;
    private String articleSummary;
    private long articleCommentCount;
    private long articleViewCount;

    @Builder
    public ArticleViewDto(UUID id, UUID viewedBy, LocalDateTime createdAt, UUID articleId,
            String source, String sourceUrl, String articleTitle, LocalDateTime articlePublishedDate,
            String articleSummary, long articleCommentCount, long articleViewCount) {
        this.id = id;
        this.viewedBy = viewedBy;
        this.createdAt = createdAt;
        this.articleId = articleId;
        this.source = source;
        this.sourceUrl = sourceUrl;
        this.articleTitle = articleTitle;
        this.articlePublishedDate = articlePublishedDate;
        this.articleSummary = articleSummary;
        this.articleCommentCount = articleCommentCount;
        this.articleViewCount = articleViewCount;
    }
}
