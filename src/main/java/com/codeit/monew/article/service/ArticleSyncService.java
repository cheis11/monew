package com.codeit.monew.article.service;

import com.codeit.monew.article.client.naver.NaverNewsClient;
import com.codeit.monew.article.client.naver.dto.NaverNewsSearchRequest;
import com.codeit.monew.article.client.naver.dto.NaverNewsSearchResponse;
import com.codeit.monew.article.entity.Article;
import com.codeit.monew.article.repository.ArticleRepository;
import com.codeit.monew.interest.entity.Interest;
import com.codeit.monew.interest.entity.Subscription;
import com.codeit.monew.interest.repository.InterestRepository;
import com.codeit.monew.interest.repository.SubscriptionRepository;
import com.codeit.monew.notification.service.NotificationService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleSyncService {

    private final NaverNewsClient naverNewsClient;
    private final com.codeit.monew.article.client.hankyung.HankyungNewsClient hankyungNewsClient;
    private final com.codeit.monew.article.client.chosun.ChosunNewsClient chosunNewsClient;
    private final com.codeit.monew.article.client.yonhap.YonhapNewsClient yonhapNewsClient;
    private final ArticleRepository articleRepository;
    private final InterestRepository interestRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;

    private static final DateTimeFormatter NAVER_DATE_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;

    // Runs at the 0th minute of every hour
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void syncNewsArticles() {
        log.info("Starting scheduled news article sync");

        // Collect all unique keywords from all interests
        List<Interest> interests = interestRepository.findAll();
        Set<String> keywords = interests.stream()
                .map(Interest::getKeywords)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        if (keywords.isEmpty()) {
            log.info("No keywords found from interests. Skipping scheduled news article sync.");
            return;
        }

        Set<Article> newlySavedArticles = new java.util.HashSet<>();

        // 1. Sync from Naver API
        for (String keyword : keywords) {
            try {
                int totalFetched = 0;
                for (int start = 1; start <= 1000; start += 100) {
                    NaverNewsSearchRequest request = NaverNewsSearchRequest.builder()
                            .query(keyword)
                            .display(100)
                            .start(start)
                            .sort("sim")
                            .build();

                    NaverNewsSearchResponse response = naverNewsClient.searchNews(request);
                    if (response.items() == null || response.items().isEmpty()) {
                        break; // No more results for this keyword
                    }
                    newlySavedArticles.addAll(saveArticlesFromResponse(response));
                    totalFetched += response.items().size();

                    // Added a sleep to prevent hitting Naver API rate limits too quickly
                    Thread.sleep(100);
                }
                log.info("Fetched {} articles from Naver for keyword: {}", totalFetched, keyword);
            } catch (Exception e) {
                log.error("Failed to sync Naver news for keyword: {}", keyword, e);
            }
        }

        // 2. Sync from Hankyung RSS feed
        try {
            log.info("Starting news article sync from Hankyung RSS");
            List<com.codeit.monew.article.client.hankyung.HankyungNewsClient.HankyungNewsItem> hankyungItems = hankyungNewsClient.fetchNews();
            int hankyungSavedCount = 0;
            for (com.codeit.monew.article.client.hankyung.HankyungNewsClient.HankyungNewsItem item : hankyungItems) {
                if (item.link() == null || item.link().isBlank() || articleRepository.existsBySourceUrl(item.link())) {
                    continue;
                }

                // Filter by interest keywords
                String titleLower = item.title() != null ? item.title().toLowerCase() : "";
                boolean matchesKeyword = false;
                for (String keyword : keywords) {
                    if (keyword != null && !keyword.isBlank() && titleLower.contains(keyword.toLowerCase())) {
                        matchesKeyword = true;
                        break;
                    }
                }

                if (matchesKeyword) {
                    Article article = Article.builder()
                            .source("HANKYUNG")
                            .sourceUrl(item.link())
                            .title(item.title())
                            .summary("") // Hankyung RSS has no description/summary field in RSS items
                            .publishDate(item.pubDate())
                            .build();

                    Article savedArticle = articleRepository.save(article);
                    newlySavedArticles.add(savedArticle);
                    hankyungSavedCount++;
                }
            }
            log.info("Finished news article sync from Hankyung RSS. Saved {} articles.", hankyungSavedCount);
        } catch (Exception e) {
            log.error("Failed to sync news from Hankyung RSS", e);
        }

        // 3. Sync from Chosun RSS feed
        try {
            log.info("Starting news article sync from Chosun RSS");
            List<com.codeit.monew.article.client.chosun.ChosunNewsClient.ChosunNewsItem> chosunItems = chosunNewsClient.fetchNews();
            int chosunSavedCount = 0;
            for (com.codeit.monew.article.client.chosun.ChosunNewsClient.ChosunNewsItem item : chosunItems) {
                if (item.link() == null || item.link().isBlank() || articleRepository.existsBySourceUrl(item.link())) {
                    continue;
                }

                // Filter by interest keywords
                String titleLower = item.title() != null ? item.title().toLowerCase() : "";
                boolean matchesKeyword = false;
                for (String keyword : keywords) {
                    if (keyword != null && !keyword.isBlank() && titleLower.contains(keyword.toLowerCase())) {
                        matchesKeyword = true;
                        break;
                    }
                }

                if (matchesKeyword) {
                    Article article = Article.builder()
                            .source("CHOSUN")
                            .sourceUrl(item.link())
                            .title(item.title())
                            .summary("") // Chosun RSS item description is empty/blank in the feed
                            .publishDate(item.pubDate())
                            .build();

                    Article savedArticle = articleRepository.save(article);
                    newlySavedArticles.add(savedArticle);
                    chosunSavedCount++;
                }
            }
            log.info("Finished news article sync from Chosun RSS. Saved {} articles.", chosunSavedCount);
        } catch (Exception e) {
            log.error("Failed to sync news from Chosun RSS", e);
        }

        // 4. Sync from Yonhap RSS feed
        try {
            log.info("Starting news article sync from Yonhap RSS");
            List<com.codeit.monew.article.client.yonhap.YonhapNewsClient.YonhapNewsItem> yonhapItems = yonhapNewsClient.fetchNews();
            int yonhapSavedCount = 0;
            for (com.codeit.monew.article.client.yonhap.YonhapNewsClient.YonhapNewsItem item : yonhapItems) {
                if (item.link() == null || item.link().isBlank() || articleRepository.existsBySourceUrl(item.link())) {
                    continue;
                }

                // Filter by interest keywords
                String titleLower = item.title() != null ? item.title().toLowerCase() : "";
                boolean matchesKeyword = false;
                for (String keyword : keywords) {
                    if (keyword != null && !keyword.isBlank() && titleLower.contains(keyword.toLowerCase())) {
                        matchesKeyword = true;
                        break;
                    }
                }

                if (matchesKeyword) {
                    String cleanSummary = stripHtmlTags(item.description());
                    Article article = Article.builder()
                            .source("YONHAP")
                            .sourceUrl(item.link())
                            .title(item.title())
                            .summary(cleanSummary)
                            .publishDate(item.pubDate())
                            .build();

                    Article savedArticle = articleRepository.save(article);
                    newlySavedArticles.add(savedArticle);
                    yonhapSavedCount++;
                }
            }
            log.info("Finished news article sync from Yonhap RSS. Saved {} articles.", yonhapSavedCount);
        } catch (Exception e) {
            log.error("Failed to sync news from Yonhap RSS", e);
        }

        // Create notifications for subscribed interests
        if (!newlySavedArticles.isEmpty()) {
            for (Interest interest : interests) {
                long matchedCount = newlySavedArticles.stream()
                        .filter(article -> matchesInterest(article, interest))
                        .count();

                if (matchedCount > 0) {
                    List<Subscription> subscriptions = subscriptionRepository.findByInterestId(interest.getId());
                    for (Subscription sub : subscriptions) {
                        notificationService.createNotification(
                                sub.getUser(),
                                String.format("[%s]와 관련된 기사가 %d건 등록되었습니다.", interest.getName(), matchedCount),
                                "interest",
                                interest.getId()
                        );
                    }
                }
            }
        }

        log.info("Finished scheduled news article sync");
    }

    private boolean matchesInterest(Article article, Interest interest) {
        if (interest.getKeywords() == null || interest.getKeywords().isEmpty()) {
            return false;
        }
        String title = article.getTitle() != null ? article.getTitle().toLowerCase() : "";
        String summary = article.getSummary() != null ? article.getSummary().toLowerCase() : "";
        for (String keyword : interest.getKeywords()) {
            if (keyword != null && !keyword.isBlank()) {
                String k = keyword.toLowerCase();
                if (title.contains(k) || summary.contains(k)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Article> saveArticlesFromResponse(NaverNewsSearchResponse response) {
        List<Article> saved = new java.util.ArrayList<>();
        if (response.items() == null || response.items().isEmpty()) {
            return saved;
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

            Article savedArticle = articleRepository.save(article);
            saved.add(savedArticle);
        }
        return saved;
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
