package com.codeit.monew.comment.service;

import com.codeit.monew.comment.dto.CommentLikeDto;
import com.codeit.monew.comment.entity.Comment;
import com.codeit.monew.comment.entity.CommentLike;
import com.codeit.monew.comment.repository.CommentLikeRepository;
import com.codeit.monew.comment.repository.CommentRepository;
import com.codeit.monew.common.exception.ConflictException;
import com.codeit.monew.common.exception.NotFoundException;
import com.codeit.monew.user.entity.User;
import com.codeit.monew.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentLikeDto likeComment(UUID commentId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new ConflictException("이미 좋아요를 누른 댓글입니다.");
        }

        CommentLike commentLike = CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();

        CommentLike savedLike = commentLikeRepository.save(commentLike);
        comment.increaseLikeCount();

        return CommentLikeDto.builder()
                .id(savedLike.getId())
                .likedBy(user.getId())
                .createdAt(savedLike.getCreatedAt())
                .commentId(comment.getId())
                .articleId(comment.getArticle().getId())
                .commentUserId(comment.getUser().getId())
                .commentUserNickname(comment.getUser().getNickname())
                .commentContent(comment.getContent())
                .commentLikeCount(comment.getLikeCount())
                .commentCreatedAt(comment.getCreatedAt())
                .build();
    }

    @Transactional
    public void unlikeComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        CommentLike commentLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new NotFoundException("좋아요 내역을 찾을 수 없습니다."));

        commentLikeRepository.delete(commentLike);
        comment.decreaseLikeCount();
    }
}
