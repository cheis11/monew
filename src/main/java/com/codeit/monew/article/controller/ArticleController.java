package com.codeit.monew.article.controller;

import com.codeit.monew.article.dto.ArticleViewDto;
import com.codeit.monew.article.service.ArticleViewService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleViewService articleViewService;

    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleViewDto> recordArticleView(
            @PathVariable UUID articleId,
            @RequestHeader(value = "Monew-Request-User-Id") UUID userId) {

        ArticleViewDto result = articleViewService.recordArticleView(articleId, userId);
        return ResponseEntity.ok(result);
    }
}
