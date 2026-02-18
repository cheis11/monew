package com.codeit.monew.user.dto;

import com.codeit.monew.article.dto.ArticleViewDto;
import com.codeit.monew.comment.dto.CommentActivityDto;
import com.codeit.monew.comment.dto.CommentLikeActivityDto;
import com.codeit.monew.interest.dto.SubscriptionDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserActivityDto {

    private UUID id;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
    private List<SubscriptionDto> subscriptions;
    private List<CommentActivityDto> comments;
    private List<CommentLikeActivityDto> commentLikes;
    private List<ArticleViewDto> articleViews;

    @Builder
    public UserActivityDto(UUID id, String email, String nickname, LocalDateTime createdAt,
            List<SubscriptionDto> subscriptions, List<CommentActivityDto> comments,
            List<CommentLikeActivityDto> commentLikes, List<ArticleViewDto> articleViews) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.createdAt = createdAt;
        this.subscriptions = subscriptions;
        this.comments = comments;
        this.commentLikes = commentLikes;
        this.articleViews = articleViews;
    }
}
