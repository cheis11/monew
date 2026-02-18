package com.codeit.monew.article.entity;

import com.codeit.monew.common.entity.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@Table(name = "article")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article extends AbstractEntity {

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String sourceUrl;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime publishDate;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private long viewCount;

    @Builder
    public Article(String source, String sourceUrl, String title, LocalDateTime publishDate,
            String summary) {
        this.source = source;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.publishDate = publishDate;
        this.summary = summary;
        this.viewCount = 0;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}
