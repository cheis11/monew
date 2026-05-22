package com.codeit.monew.user.service;

import com.codeit.monew.common.exception.NotFoundException;
import com.codeit.monew.common.exception.UnauthorizedException;
import com.codeit.monew.user.dto.UserDto;
import com.codeit.monew.user.dto.UserLoginRequest;
import com.codeit.monew.user.dto.UserRegisterRequest;
import com.codeit.monew.user.entity.User;
import com.codeit.monew.user.repository.UserRepository;
import com.codeit.monew.user.dto.UserActivityDto;
import com.codeit.monew.interest.repository.SubscriptionRepository;
import com.codeit.monew.comment.repository.CommentRepository;
import com.codeit.monew.comment.repository.CommentLikeRepository;
import com.codeit.monew.article.repository.ArticleViewRepository;
import com.codeit.monew.comment.dto.CommentActivityDto;
import com.codeit.monew.comment.dto.CommentLikeActivityDto;
import com.codeit.monew.article.dto.ArticleViewDto;
import com.codeit.monew.interest.dto.SubscriptionDto;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ArticleViewRepository articleViewRepository;

    @Transactional
    public UserDto registerUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        // TODO: Password should be encrypted later using PasswordEncoder
        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(request.getPassword())
                .build();

        User savedUser = userRepository.save(user);

        return UserDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public UserDto login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        // TODO: Password check should use PasswordEncoder later
        if (!user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .build();
    }
    @Transactional
    public UserDto updateUser(java.util.UUID userId, com.codeit.monew.user.dto.UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        user.update(request.getNickname());
        
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteUser(java.util.UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void hardDeleteUser(java.util.UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userRepository.hardDeleteById(userId);
    }

    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000) // Run every 1 minute
    @Transactional
    public void restoreDeletedUsers() {
        // Prototype requirement: restore users soft-deleted > 5 minutes ago
        java.time.LocalDateTime threshold = java.time.LocalDateTime.now().minusMinutes(5);
        int restoredCount = userRepository.restoreDeletedUsersBefore(threshold);
        if (restoredCount > 0) {
            // Logging can be added here
            System.out.println("Restored " + restoredCount + " users deleted before " + threshold);
        }
    }

    @Transactional(readOnly = true)
    public UserActivityDto getUserActivity(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저 정보 없음"));

        List<SubscriptionDto> subscriptions = subscriptionRepository.findByUserId(userId).stream()
                .map(sub -> SubscriptionDto.builder()
                        .id(sub.getId())
                        .interestId(sub.getInterest().getId())
                        .interestName(sub.getInterest().getName())
                        .interestKeywords(sub.getInterest().getKeywords())
                        .interestSubscriberCount(sub.getInterest().getSubscriberCount())
                        .createdAt(sub.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        List<CommentActivityDto> comments = commentRepository.findByUserId(userId).stream()
                .map(comment -> CommentActivityDto.builder()
                        .id(comment.getId())
                        .articleId(comment.getArticle().getId())
                        .articleTitle(comment.getArticle().getTitle())
                        .userId(comment.getUser().getId())
                        .userNickname(comment.getUser().getNickname())
                        .content(comment.getContent())
                        .likeCount(comment.getLikeCount())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        List<CommentLikeActivityDto> commentLikes = commentLikeRepository.findByUserId(userId).stream()
                .map(like -> CommentLikeActivityDto.builder()
                        .id(like.getId())
                        .createdAt(like.getCreatedAt())
                        .commentId(like.getComment().getId())
                        .articleId(like.getComment().getArticle().getId())
                        .articleTitle(like.getComment().getArticle().getTitle())
                        .commentUserId(like.getComment().getUser().getId())
                        .commentUserNickname(like.getComment().getUser().getNickname())
                        .commentContent(like.getComment().getContent())
                        .commentLikeCount(like.getComment().getLikeCount())
                        .commentCreatedAt(like.getComment().getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        List<ArticleViewDto> articleViews = articleViewRepository.findByUserId(userId).stream()
                .map(view -> ArticleViewDto.builder()
                        .id(view.getId())
                        .viewedBy(view.getUser().getId())
                        .createdAt(view.getCreatedAt())
                        .articleId(view.getArticle().getId())
                        .source(view.getArticle().getSource())
                        .sourceUrl(view.getArticle().getSourceUrl())
                        .articleTitle(view.getArticle().getTitle())
                        .articlePublishedDate(view.getArticle().getPublishDate())
                        .articleSummary(view.getArticle().getSummary())
                        .articleCommentCount(commentRepository.countByArticleId(view.getArticle().getId()))
                        .articleViewCount(view.getArticle().getViewCount())
                        .build())
                .collect(Collectors.toList());

        return UserActivityDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .subscriptions(subscriptions)
                .comments(comments)
                .commentLikes(commentLikes)
                .articleViews(articleViews)
                .build();
    }
}
