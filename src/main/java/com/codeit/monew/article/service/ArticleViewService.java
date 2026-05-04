package com.codeit.monew.article.service;

import com.codeit.monew.article.dto.ArticleViewDto;
import com.codeit.monew.article.entity.Article;
import com.codeit.monew.article.entity.ArticleView;
import com.codeit.monew.article.repository.ArticleRepository;
import com.codeit.monew.article.repository.ArticleViewRepository;
import com.codeit.monew.common.exception.NotFoundException;
import com.codeit.monew.user.entity.User;
import com.codeit.monew.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private final ArticleViewRepository articleViewRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    public ArticleViewDto recordArticleView(UUID articleId, UUID userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NotFoundException("댓글 정보 없음"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저 정보 없음"));

        article.increaseViewCount();

        ArticleView articleView = ArticleView.builder()
                .article(article)
                .user(user)
                .build();

        articleViewRepository.save(articleView);

        return ArticleViewDto.builder()
                .id(articleView.getId())
                .viewedBy(user.getId())
                .createdAt(articleView.getCreatedAt())
                .articleId(article.getId())
                .source(article.getSource())
                .sourceUrl(article.getSourceUrl())
                .articleTitle(article.getTitle())
                .articlePublishedDate(article.getPublishDate())
                .articleSummary(article.getSummary())
                .articleCommentCount(0) // Currently no comment feature implemented
                .articleViewCount(article.getViewCount())
                .build();
    }
}
