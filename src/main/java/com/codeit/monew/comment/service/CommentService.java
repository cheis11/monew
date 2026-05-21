package com.codeit.monew.comment.service;

import com.codeit.monew.article.entity.Article;
import com.codeit.monew.article.repository.ArticleRepository;
import com.codeit.monew.comment.dto.CommentDto;
import com.codeit.monew.comment.dto.CommentRegisterRequest;
import com.codeit.monew.comment.dto.CommentUpdateRequest;
import com.codeit.monew.comment.dto.CursorPageResponseCommentDto;
import com.codeit.monew.comment.entity.Comment;
import com.codeit.monew.comment.repository.CommentLikeRepository;
import com.codeit.monew.comment.repository.CommentRepository;
import com.codeit.monew.comment.repository.CommentSpecification;
import com.codeit.monew.common.exception.NotFoundException;
import com.codeit.monew.common.exception.UnauthorizedException;
import com.codeit.monew.user.entity.User;
import com.codeit.monew.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public CommentDto registerComment(CommentRegisterRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Article article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new NotFoundException("Article not found"));

        Comment comment = Comment.builder()
                .article(article)
                .user(user)
                .content(request.getContent())
                .build();

        Comment savedComment = commentRepository.save(comment);

        return CommentDto.builder()
                .id(savedComment.getId())
                .articleId(savedComment.getArticle().getId())
                .userId(savedComment.getUser().getId())
                .userNickname(savedComment.getUser().getNickname())
                .content(savedComment.getContent())
                .likeCount(savedComment.getLikeCount())
                .likedByMe(false)
                .createdAt(savedComment.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public CursorPageResponseCommentDto getComments(
            UUID articleId,
            String orderBy,
            String direction,
            String cursor,
            LocalDateTime after,
            int limit,
            UUID userId) {

        String validOrderBy = "likeCount".equals(orderBy) ? orderBy : "createdAt";
        String validDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        Long cursorLikeCount = null;
        LocalDateTime afterTime = after;

        if (cursor != null && !cursor.isBlank()) {
            UUID cursorId = UUID.fromString(cursor);
            // soft-deleted 되지 않은 활성화 상태의 댓글 중 커서 대상을 찾아야 하므로 일반 findById 사용
            Comment cursorComment = commentRepository.findById(cursorId)
                    .orElseThrow(() -> new NotFoundException("Cursor comment not found"));
            cursorLikeCount = cursorComment.getLikeCount();
            if (afterTime == null) {
                afterTime = cursorComment.getCreatedAt();
            }
        }

        Page<Comment> commentPage = commentRepository.findAll(
                CommentSpecification.filterComments(
                        articleId, validOrderBy, validDirection, cursor, afterTime, cursorLikeCount),
                PageRequest.of(0, limit + 1));

        List<Comment> comments = commentPage.getContent();
        boolean hasNext = comments.size() > limit;
        if (hasNext) {
            comments = comments.subList(0, limit);
        }

        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> {
                    boolean likedByMe = false;
                    if (userId != null) {
                        likedByMe = commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), userId);
                    }
                    return CommentDto.builder()
                            .id(comment.getId())
                            .articleId(comment.getArticle().getId())
                            .userId(comment.getUser().getId())
                            .userNickname(comment.getUser().getNickname())
                            .content(comment.getContent())
                            .likeCount(comment.getLikeCount())
                            .likedByMe(likedByMe)
                            .createdAt(comment.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        String nextCursor = null;
        LocalDateTime nextAfter = null;

        if (!commentDtos.isEmpty() && hasNext) {
            Comment lastComment = comments.get(comments.size() - 1);
            nextCursor = lastComment.getId().toString();
            nextAfter = lastComment.getCreatedAt();
        }

        return CursorPageResponseCommentDto.builder()
                .content(commentDtos)
                .nextCursor(nextCursor)
                .nextAfter(nextAfter)
                .size(commentDtos.size())
                .totalElements(commentPage.getTotalElements())
                .hasNext(hasNext)
                .build();
    }

    @Transactional
    public CommentDto updateComment(UUID commentId, UUID userId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("댓글 수정 권한이 없습니다.");
        }

        comment.update(request.getContent());

        boolean likedByMe = false;
        if (userId != null) {
            likedByMe = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        }

        return CommentDto.builder()
                .id(comment.getId())
                .articleId(comment.getArticle().getId())
                .userId(comment.getUser().getId())
                .userNickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .likedByMe(likedByMe)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        commentRepository.delete(comment);
    }

    @Transactional
    public void hardDeleteComment(UUID commentId) {
        if (commentRepository.countByIdIncludingDeleted(commentId) == 0) {
            throw new NotFoundException("Comment not found");
        }
        commentLikeRepository.deleteByCommentId(commentId);
        commentRepository.hardDeleteById(commentId);
    }
}
