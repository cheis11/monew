package com.codeit.monew.article.service;

import com.codeit.monew.article.dto.ArticleDto;
import com.codeit.monew.article.dto.ArticleRestoreResultDto;
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
    private final com.codeit.monew.interest.repository.SubscriptionRepository subscriptionRepository;
    private final com.codeit.monew.comment.repository.CommentRepository commentRepository;
    private final com.codeit.monew.article.repository.ArticleViewRepository articleViewRepository;

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

        // Query keywords
        List<String> searchKeywords = new java.util.ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            searchKeywords.add(keyword);
        } else if (interestId != null) {
            Interest interest = interestRepository.findById(interestId)
                    .orElseThrow(() -> new NotFoundException("Interest not found"));
            searchKeywords.addAll(interest.getKeywords());
        } else if (userId != null) {
            List<Interest> subscribedInterests = subscriptionRepository.findInterestsByUserId(userId);
            if (subscribedInterests.isEmpty()) {
                // Return empty response if user has no subscriptions
                return CursorPageResponseArticleDto.builder()
                        .content(java.util.Collections.emptyList())
                        .nextCursor(null)
                        .nextAfter(null)
                        .size(0)
                        .totalElements(0)
                        .hasNext(false)
                        .build();
            }
            for (Interest interest : subscribedInterests) {
                searchKeywords.addAll(interest.getKeywords());
            }
        }

        // Fetch articles based on specification + limit (limit + 1 to check hasNext)
        Page<Article> articlePage = articleRepository.findAll(
                ArticleSpecification.filterArticles(
                        searchKeywords, interestId, sourceIn, publishDateFrom, publishDateTo, validOrderBy,
                        validDirection, cursor, after),
                PageRequest.of(0, limit + 1));

        List<Article> articles = articlePage.getContent();
        boolean hasNext = articles.size() > limit;
        if (hasNext) {
            articles = articles.subList(0, limit);
        }

        List<ArticleDto> articleDtos = articles.stream()
                .map(article -> {
                    boolean viewedByMe = false;
                    if (userId != null) {
                        viewedByMe = articleViewRepository.existsByArticleIdAndUserId(article.getId(), userId);
                    }
                    long commentCount = commentRepository.countByArticleId(article.getId());
                    return ArticleDto.builder()
                            .id(article.getId())
                            .source(article.getSource())
                            .sourceUrl(article.getSourceUrl())
                            .title(article.getTitle())
                            .publishDate(article.getPublishDate())
                            .summary(article.getSummary())
                            .commentCount(commentCount)
                            .viewCount(article.getViewCount())
                            .viewedByMe(viewedByMe)
                            .build();
                })
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

    @Transactional(readOnly = true)
    public ArticleDto getArticle(UUID articleId, UUID userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("뉴스 기사를 찾을 수 없습니다."));

        boolean viewedByMe = false;
        if (userId != null) {
            viewedByMe = articleViewRepository.existsByArticleIdAndUserId(articleId, userId);
        }

        long commentCount = commentRepository.countByArticleId(articleId);

        return ArticleDto.builder()
                .id(article.getId())
                .source(article.getSource())
                .sourceUrl(article.getSourceUrl())
                .title(article.getTitle())
                .publishDate(article.getPublishDate())
                .summary(article.getSummary())
                .commentCount(commentCount)
                .viewCount(article.getViewCount())
                .viewedByMe(viewedByMe)
                .build();
    }

    @Transactional
    public void deleteArticle(UUID articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("뉴스 기사를 찾을 수 없습니다."));
        articleRepository.delete(article);
    }

    @Transactional
    public void hardDeleteArticle(UUID articleId) {
        if (!articleRepository.existsById(articleId)) {
            throw new NotFoundException("뉴스 기사를 찾을 수 없습니다.");
        }
        articleRepository.hardDeleteById(articleId);
    }

    @Transactional(readOnly = true)
    public List<String> getSources() {
        List<String> dbSources = articleRepository.findDistinctSource();
        List<String> defaultSources = List.of("NAVER", "HANKYUNG", "CHOSUN", "YONHAP");
        
        java.util.Set<String> allSources = new java.util.LinkedHashSet<>(defaultSources);
        allSources.addAll(dbSources);
        
        return new java.util.ArrayList<>(allSources);
    }

    @Transactional
    public List<ArticleRestoreResultDto> restoreArticles(LocalDateTime from, LocalDateTime to) {
        // TODO: S3 backup is not implemented yet. This is a stub.
        return java.util.Collections.emptyList();
    }
}
