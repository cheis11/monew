package com.codeit.monew.article.dto;

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
public class ArticleRestoreResultDto {

    private LocalDateTime restoreDate;
    private List<String> restoredArticleIds;
    private long restoredArticleCount;

    @Builder
    public ArticleRestoreResultDto(LocalDateTime restoreDate, List<String> restoredArticleIds,
            long restoredArticleCount) {
        this.restoreDate = restoreDate;
        this.restoredArticleIds = restoredArticleIds;
        this.restoredArticleCount = restoredArticleCount;
    }
}
