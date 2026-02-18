package com.codeit.monew.article.entity;

import com.codeit.monew.common.entity.AbstractEntity;
import com.codeit.monew.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@Table(name = "article_view")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleView extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    @ToString.Exclude
    private Article article;

    @Builder
    public ArticleView(User user, Article article) {
        this.user = user;
        this.article = article;
    }
}
