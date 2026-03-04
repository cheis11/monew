package com.codeit.monew.article.client.naver.dto;

import java.util.List;

public record NaverNewsSearchResponse(
                String lastBuildDate,
                Integer total,
                Integer start,
                Integer display,
                List<NaverNewsItem> items) {
        public record NaverNewsItem(
                        String title,
                        String originallink,
                        String link,
                        String description,
                        String pubDate) {
        }
}
