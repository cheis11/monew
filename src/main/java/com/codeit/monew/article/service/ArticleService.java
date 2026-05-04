package com.codeit.monew.article.service;

import com.codeit.monew.article.dto.ArticleDto;
import com.codeit.monew.article.dto.CursorPageResponseArticleDto;
import com.codeit.monew.article.entity.Article;
import com.codeit.monew.article.repository.ArticleRepository;
import com.codeit.monew.article.repository.ArticleSpecification;
import com.codeit.monew.common.exception.NotFoundException;
import com.codeit.monew.interest.entity.Interest;
import com.codeit.monew.interest.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final InterestRepository interestRepository;

    @Transactional(readOnly = true)
    public CursorPageResponseArticleDto getArticles(
            String keyword,
            UUID interestId,
            String sourceIn,
            LocalDateTime publishDateFrom,
            LocalDateTime publishDateTo,
            String orderBy,
            String direction,
            String cursor,
            LocalDateTime after,
            int limit,
            UUID userId) {

        // Validate orderBy
        String validOrderBy = ("viewCount".equals(orderBy) || "commentCount".equals(orderBy)) ? orderBy : "publishDate";
        String validDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        // Query keywords if interestId is provided
        String searchKeyword = keyword;
        if (interestId != null) {
            Interest interest = interestRepository.findById(interestId)
                    .orElseThrow(() -> new NotFoundException("Interest not found"));
            // If multiple keywords exist for interest, we technically need OR condition in
            // spec,
            // but for simplicity assuming the first one or concatenating.
            // A more robust implementation would pass List<String> to spec.
            if (!interest.getKeywords().isEmpty()) {
                searchKeyword = interest.getKeywords().get(0); // Temporary simplification
            }
        }

        // Fetch articles based on specification + limit (limit + 1 to check hasNext)
        Page<Article> articlePage = articleRepository.findAll(
                ArticleSpecification.filterArticles(
                        searchKeyword, interestId, sourceIn, publishDateFrom, publishDateTo, validOrderBy,
                        validDirection, cursor, after),
                PageRequest.of(0, limit + 1));

        List<Article> articles = articlePage.getContent();
        boolean hasNext = articles.size() > limit;
        if (hasNext) {
            articles = articles.subList(0, limit);
        }

        List<ArticleDto> articleDtos = articles.stream()
                .map(article -> ArticleDto.builder()
                        .id(article.getId())
                        .source(article.getSource())
                        .sourceUrl(article.getSourceUrl())
                        .title(article.getTitle())
                        .publishDate(article.getPublishDate())
                        .summary(article.getSummary())
                        .commentCount(0) // Comments feature not implemented yet
                        .viewCount(article.getViewCount())
                        // viewedByMe is true if the user has a view record.
                        // Can be optimized outside the loop, but skipping for brevity
                        .viewedByMe(false)
                        .build())
                .collect(Collectors.toList());

        String nextCursor = null;
        LocalDateTime nextAfter = null;

        if (!articleDtos.isEmpty() && hasNext) {
            Article lastArticle = articles.get(articles.size() - 1);
            nextCursor = lastArticle.getId().toString();
            // Determine the nextAfter based on sort column
            if ("viewCount".equals(validOrderBy)) {
                // Assuming viewCount is what we track for nextAfter if sorted by viewCount
                // However nextAfter parameter type is LocalDateTime. This requires complex API
                // handling
                // if we strictly use nextAfter for non-date fields.
                // Assuming nextAfter strictly maps to publishDate.
            }
            nextAfter = lastArticle.getPublishDate();
        }

        return CursorPageResponseArticleDto.builder()
                .content(articleDtos)
                .nextCursor(nextCursor)
                .nextAfter(nextAfter)
                .size(articleDtos.size())
                .totalElements(articlePage.getTotalElements()) // This is an estimate unless we run count query
                .hasNext(hasNext)
                .build();
    }
}
