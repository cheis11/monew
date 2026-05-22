package com.codeit.monew.article.repository;

import com.codeit.monew.article.entity.ArticleView;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleViewRepository extends JpaRepository<ArticleView, UUID> {
    java.util.List<ArticleView> findByUserId(UUID userId);
    boolean existsByArticleIdAndUserId(UUID articleId, UUID userId);
}
