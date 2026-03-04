package com.codeit.monew.article.service;

import com.codeit.monew.article.client.naver.NaverNewsClient;
import com.codeit.monew.article.client.naver.dto.NaverNewsSearchRequest;
import com.codeit.monew.article.client.naver.dto.NaverNewsSearchResponse;
import com.codeit.monew.article.entity.Article;
import com.codeit.monew.article.repository.ArticleRepository;
import com.codeit.monew.interest.entity.Interest;
import com.codeit.monew.interest.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleSyncService {

    private final NaverNewsClient naverNewsClient;
    private final ArticleRepository articleRepository;
    private final InterestRepository interestRepository;

    private static final DateTimeFormatter NAVER_DATE_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;

    // Runs at the 0th minute of every hour
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void syncNewsArticles() {
        log.info("Starting scheduled news article sync from Naver API");

        // Collect all unique keywords from all interests
        List<Interest> interests = interestRepository.findAll();
        Set<String> keywords = interests.stream()
                .map(Interest::getKeywords)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        if (keywords.isEmpty()) {
            log.info("No keywords found. Searching for default keyword: '경제'");
            keywords.add("경제");
        }

        for (String keyword : keywords) {
            try {
                NaverNewsSearchRequest request = NaverNewsSearchRequest.builder()
                        .query(keyword)
                        .display(10)
                        .sort("sim")
                        .build();

                NaverNewsSearchResponse response = naverNewsClient.searchNews(request);
                saveArticlesFromResponse(response);
            } catch (Exception e) {
                log.error("Failed to sync news for keyword: {}", keyword, e);
            }
        }
        log.info("Finished scheduled news article sync");
    }

    private void saveArticlesFromResponse(NaverNewsSearchResponse response) {
        if (response.items() == null || response.items().isEmpty()) {
            return;
        }

        for (NaverNewsSearchResponse.NaverNewsItem item : response.items()) {
            String originallink = item.originallink();

            if (originallink == null || originallink.isBlank() || articleRepository.existsBySourceUrl(originallink)) {
                continue;
            }

            // Remove HTML tags from title and description that Naver API includes (e.g.
            // <b>주식</b>)
            String cleanTitle = stripHtmlTags(item.title());
            String cleanDescription = stripHtmlTags(item.description());

            LocalDateTime pubDate = parseDate(item.pubDate());

            Article article = Article.builder()
                    .source("NAVER")
                    .sourceUrl(originallink)
                    .title(cleanTitle)
                    .summary(cleanDescription)
                    .publishDate(pubDate)
                    .build();

            articleRepository.save(article);
        }
    }

    private String stripHtmlTags(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("<[^>]*>", "");
    }

    private LocalDateTime parseDate(String pubDateString) {
        try {
            return LocalDateTime.parse(pubDateString, NAVER_DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("Failed to parse date: {}. Using current time instead.", pubDateString);
            return LocalDateTime.now();
        }
    }
}
