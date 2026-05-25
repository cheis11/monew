package com.codeit.monew.article.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeit.monew.article.entity.Article;
import com.codeit.monew.article.repository.ArticleRepository;
import com.codeit.monew.interest.entity.Interest;
import com.codeit.monew.interest.repository.InterestRepository;
import com.codeit.monew.article.dto.CursorPageResponseArticleDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArticleSyncServiceTest {

    @Autowired
    private ArticleSyncService articleSyncService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Test
    void testSyncNewsArticles() {
        articleRepository.deleteAll();
        interestRepository.deleteAll();

        // Seed interest
        Interest interest = Interest.builder()
                .name("주식투자")
                .keywords(List.of("삼성", "코스피"))
                .build();
        interestRepository.save(interest);

        // Manually insert a mock HANKYUNG article containing the keyword
        Article mockHankyung = Article.builder()
                .source("HANKYUNG")
                .sourceUrl("https://www.hankyung.com/mock-article-123")
                .title("삼성전자, 차세대 반도체 발표")
                .summary("삼성전자가 반도체 신제품을 발표했습니다.")
                .publishDate(java.time.LocalDateTime.now())
                .build();
        articleRepository.save(mockHankyung);

        // Manually insert a mock CHOSUN article containing the keyword
        Article mockChosun = Article.builder()
                .source("CHOSUN")
                .sourceUrl("https://www.chosun.com/mock-article-123")
                .title("삼성전자 주가 최고가 돌파")
                .summary("삼성전자가 또다시 최고가를 갱신했습니다.")
                .publishDate(java.time.LocalDateTime.now())
                .build();
        articleRepository.save(mockChosun);

        // Manually insert a mock YONHAP article containing the keyword
        Article mockYonhap = Article.builder()
                .source("YONHAP")
                .sourceUrl("https://www.yonhapnewstv.co.kr/mock-article-123")
                .title("삼성전자, 외교적 합의 소식 전달")
                .summary("삼성전자가 소식을 전했습니다.")
                .publishDate(java.time.LocalDateTime.now())
                .build();
        articleRepository.save(mockYonhap);

        // Run sync (will fetch NAVER news too)
        articleSyncService.syncNewsArticles();

        List<Article> articles = articleRepository.findAll();
        System.out.println("=== Saved Articles after Sync ===");
        for (Article article : articles) {
            System.out.println("Source: " + article.getSource() + " | Title: " + article.getTitle() + " | Url: " + article.getSourceUrl());
        }
        
        assertFalse(articles.isEmpty(), "Saved articles list should not be empty");

        // Query with NAVER, HANKYUNG, CHOSUN, and YONHAP sources
        CursorPageResponseArticleDto responseAll = articleService.getArticles(
                "삼성", null, "NAVER,HANKYUNG,CHOSUN,YONHAP", null, null, "publishDate", "DESC", null, null, 50, null);
        assertFalse(responseAll.getContent().isEmpty(), "Result should not be empty when querying multiple sources");
        
        boolean hasNaver = responseAll.getContent().stream().anyMatch(a -> "NAVER".equals(a.getSource()));
        boolean hasHankyung = responseAll.getContent().stream().anyMatch(a -> "HANKYUNG".equals(a.getSource()));
        boolean hasChosun = responseAll.getContent().stream().anyMatch(a -> "CHOSUN".equals(a.getSource()));
        boolean hasYonhap = responseAll.getContent().stream().anyMatch(a -> "YONHAP".equals(a.getSource()));
        
        System.out.println("Has NAVER: " + hasNaver + " | Has HANKYUNG: " + hasHankyung + " | Has CHOSUN: " + hasChosun + " | Has YONHAP: " + hasYonhap);
        assertTrue(hasHankyung, "Mock HANKYUNG article should be returned in multi-source search");
        assertTrue(hasChosun, "Mock CHOSUN article should be returned in multi-source search");
        assertTrue(hasYonhap, "Mock YONHAP article should be returned in multi-source search");
    }
}
