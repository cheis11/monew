package com.codeit.monew.article.controller;

import com.codeit.monew.article.client.naver.NaverNewsClient;
import com.codeit.monew.article.client.naver.dto.NaverNewsSearchRequest;
import com.codeit.monew.article.client.naver.dto.NaverNewsSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test/naver-news")
@RequiredArgsConstructor
public class NaverNewsTestController {

    private final NaverNewsClient naverNewsClient;

    @GetMapping
    public NaverNewsSearchResponse searchTest(@ModelAttribute NaverNewsSearchRequest request) {
        return naverNewsClient.searchNews(request);
    }
}
