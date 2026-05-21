package com.codeit.monew.comment.controller;

import com.codeit.monew.comment.dto.CommentDto;
import com.codeit.monew.comment.dto.CommentLikeDto;
import com.codeit.monew.comment.dto.CommentRegisterRequest;
import com.codeit.monew.comment.dto.CommentUpdateRequest;
import com.codeit.monew.comment.dto.CursorPageResponseCommentDto;
import com.codeit.monew.comment.service.CommentLikeService;
import com.codeit.monew.comment.service.CommentService;
import com.codeit.monew.common.exception.NotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    @GetMapping
    public ResponseEntity<CursorPageResponseCommentDto> getComments(
            @RequestParam(required = false) UUID articleId,
            @RequestParam(defaultValue = "createdAt") String orderBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) String after,
            @RequestParam(defaultValue = "50") int limit,
            @RequestHeader(value = "Monew-Request-User-ID", required = false) UUID userId) {

        LocalDateTime afterTime = parseDateTime(after);

        CursorPageResponseCommentDto response = commentService.getComments(
                articleId, orderBy, direction, cursor, afterTime, limit, userId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommentDto> registerComment(
            @Valid @RequestBody CommentRegisterRequest request) {
        CommentDto response = commentService.registerComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable UUID commentId,
            @RequestHeader("Monew-Request-User-ID") UUID userId,
            @Valid @RequestBody CommentUpdateRequest request) {
        CommentDto response = commentService.updateComment(commentId, userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}/hard")
    public ResponseEntity<Void> hardDeleteComment(@PathVariable UUID commentId) {
        commentService.hardDeleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{commentId}/comment-likes")
    public ResponseEntity<CommentLikeDto> likeComment(
            @PathVariable UUID commentId,
            @RequestHeader("Monew-Request-User-ID") UUID userId) {
        CommentLikeDto response = commentLikeService.likeComment(commentId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}/comment-likes")
    public ResponseEntity<Void> unlikeComment(
            @PathVariable UUID commentId,
            @RequestHeader("Monew-Request-User-ID") UUID userId) {
        commentLikeService.unlikeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            if (dateStr.endsWith("Z") || dateStr.contains("+")
                    || (dateStr.contains("-") && dateStr.lastIndexOf("-") > 10)) {
                return java.time.ZonedDateTime.parse(dateStr).toLocalDateTime();
            }
            return LocalDateTime.parse(dateStr);
        } catch (Exception e) {
            throw new NotFoundException("Invalid date format: " + dateStr);
        }
    }
}
