package com.codeit.monew.article.controller;

import com.codeit.monew.article.dto.ArticleViewDto;
import com.codeit.monew.article.service.ArticleViewService;
import com.codeit.monew.common.exception.NotFoundException;
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
            @RequestParam(required = false) String publishDateFrom,
            @RequestParam(required = false) String publishDateTo,
            @RequestParam(defaultValue = "publishDate") String orderBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) String after,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader(value = "Monew-Request-User-ID", required = false) UUID userId) {

        LocalDateTime from = parseDateTime(publishDateFrom);
        LocalDateTime to = parseDateTime(publishDateTo);
        LocalDateTime afterLocal = parseDateTime(after);

        CursorPageResponseArticleDto response = articleService.getArticles(
                keyword, interestId, sourceIn, from, to,
                orderBy, direction, cursor, afterLocal, limit, userId);

        return ResponseEntity.ok(response);
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            if (dateStr.endsWith("Z") || dateStr.contains("+")
                    || (dateStr.contains("-") && dateStr.lastIndexOf("-") > 10)) {
                return java.time.ZonedDateTime.parse(dateStr).toLocalDateTime();
            }
            return LocalDateTime.parse(dateStr);
        } catch (Exception e) {
            throw new NotFoundException("Invalid date format: " + dateStr);
        }
    }

    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleViewDto> recordArticleView(
            @PathVariable UUID articleId,
            @RequestHeader(value = "Monew-Request-User-ID") UUID userId) {

        ArticleViewDto result = articleViewService.recordArticleView(articleId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<com.codeit.monew.article.dto.ArticleDto> getArticle(
            @PathVariable java.util.UUID articleId,
            @RequestHeader(value = "Monew-Request-User-ID", required = false) java.util.UUID userId) {
        
        com.codeit.monew.article.dto.ArticleDto result = articleService.getArticle(articleId, userId);
        return ResponseEntity.ok(result);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteArticle(
            @PathVariable java.util.UUID articleId) {
        
        articleService.deleteArticle(articleId);
        return ResponseEntity.noContent().build();
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{articleId}/hard")
    public ResponseEntity<Void> hardDeleteArticle(
            @PathVariable java.util.UUID articleId) {
        
        articleService.hardDeleteArticle(articleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sources")
    public ResponseEntity<java.util.List<String>> getSources() {
        java.util.List<String> sources = articleService.getSources();
        return ResponseEntity.ok(sources);
    }

    @GetMapping("/restore")
    public ResponseEntity<java.util.List<com.codeit.monew.article.dto.ArticleRestoreResultDto>> restoreArticles(
            @RequestParam String from,
            @RequestParam String to) {
        
        LocalDateTime fromTime = parseDateTime(from);
        LocalDateTime toTime = parseDateTime(to);

        java.util.List<com.codeit.monew.article.dto.ArticleRestoreResultDto> results = articleService.restoreArticles(fromTime, toTime);
        return ResponseEntity.ok(results);
    }
}
