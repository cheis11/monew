package com.codeit.monew.article.client.naver;

import com.codeit.monew.article.client.naver.dto.NaverNewsSearchRequest;
import com.codeit.monew.article.client.naver.dto.NaverNewsSearchResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NaverNewsClient {

    private final RestClient restClient;
    private static final String NAVER_NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json";

    public NaverNewsClient(NaverNewsProperties properties) {
        this.restClient = RestClient.builder()
                .baseUrl(NAVER_NEWS_API_URL)
                .defaultHeader("X-Naver-Client-Id", properties.id())
                .defaultHeader("X-Naver-Client-Secret", properties.secret())
                .build();
    }

    public NaverNewsSearchResponse searchNews(NaverNewsSearchRequest request) {
        String uri = UriComponentsBuilder.fromUriString("")
                .queryParam("query", request.query())
                .queryParam("display", request.display())
                .queryParam("start", request.start())
                .queryParam("sort", request.sort())
                .build()
                .toUriString();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(NaverNewsSearchResponse.class);
    }
}
