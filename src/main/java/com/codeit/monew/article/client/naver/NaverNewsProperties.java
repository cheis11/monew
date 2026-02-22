package com.codeit.monew.article.client.naver;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "naver.client")
public record NaverNewsProperties(
        String id,
        String secret) {
}
