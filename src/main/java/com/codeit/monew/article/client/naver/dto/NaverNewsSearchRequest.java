package com.codeit.monew.article.client.naver.dto;

import lombok.Builder;

@Builder
public record NaverNewsSearchRequest(
        String query,
        Integer display,
        Integer start,
        String sort) {
    public NaverNewsSearchRequest {
        if (display == null)
            display = 10;
        if (start == null)
            start = 1;
        if (sort == null)
            sort = "sim";
    }
}
