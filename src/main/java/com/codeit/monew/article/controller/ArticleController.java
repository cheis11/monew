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

import com.codeit.monew.article.dto.CursorPageResponseArticleDto;
import com.codeit.monew.article.service.ArticleService;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleViewService articleViewService;
    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<CursorPageResponseArticleDto> getArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID interestId,
            @RequestParam(required = false) String sourceIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishDateTo,
            @RequestParam(defaultValue = "publishDate") String orderBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader(value = "Monew-Request-User-Id", required = false) UUID userId) {

        CursorPageResponseArticleDto response = articleService.getArticles(
                keyword, interestId, sourceIn, publishDateFrom, publishDateTo,
                orderBy, direction, cursor, after, limit, userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleViewDto> recordArticleView(
            @PathVariable UUID articleId,
            @RequestHeader(value = "Monew-Request-User-Id") UUID userId) {

        ArticleViewDto result = articleViewService.recordArticleView(articleId, userId);
        return ResponseEntity.ok(result);
    }
}
