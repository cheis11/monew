package com.codeit.monew.article.repository;

import com.codeit.monew.article.entity.Article;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, UUID> {
}
