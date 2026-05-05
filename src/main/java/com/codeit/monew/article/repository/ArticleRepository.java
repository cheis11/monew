package com.codeit.monew.article.repository;

import com.codeit.monew.article.entity.Article;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArticleRepository extends JpaRepository<Article, UUID>, JpaSpecificationExecutor<Article> {
    boolean existsBySourceUrl(String sourceUrl);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "DELETE FROM article WHERE id = :articleId", nativeQuery = true)
    void hardDeleteById(@org.springframework.data.repository.query.Param("articleId") UUID articleId);
}
