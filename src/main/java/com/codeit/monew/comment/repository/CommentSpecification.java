package com.codeit.monew.comment.repository;

import com.codeit.monew.comment.entity.Comment;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class CommentSpecification {

    public static Specification<Comment> filterComments(
            UUID articleId,
            String orderBy,
            String direction,
            String cursor,
            LocalDateTime after,
            Long cursorLikeCount) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Article ID filter
            if (articleId != null) {
                predicates.add(cb.equal(root.get("article").get("id"), articleId));
            }

            // 2. Cursor Pagination logic
            if (cursor != null && !cursor.isBlank() && after != null) {
                UUID cursorId = UUID.fromString(cursor);

                if ("likeCount".equals(orderBy)) {
                    // Sort field hierarchy: likeCount -> createdAt -> id
                    Predicate likeEqualCondition = cb.equal(root.get("likeCount"), cursorLikeCount);
                    Predicate createdEqualCondition = cb.equal(root.get("createdAt"), after);

                    if ("ASC".equalsIgnoreCase(direction)) {
                        Predicate likeGreater = cb.greaterThan(root.get("likeCount"), cursorLikeCount);
                        Predicate createdGreater = cb.greaterThan(root.get("createdAt"), after);
                        Predicate idGreater = cb.greaterThan(root.get("id"), cursorId);

                        Predicate tieBreaker2 = cb.and(likeEqualCondition, createdGreater);
                        Predicate tieBreaker3 = cb.and(likeEqualCondition, createdEqualCondition, idGreater);

                        predicates.add(cb.or(likeGreater, tieBreaker2, tieBreaker3));
                    } else { // DESC
                        Predicate likeLess = cb.lessThan(root.get("likeCount"), cursorLikeCount);
                        Predicate createdLess = cb.lessThan(root.get("createdAt"), after);
                        Predicate idLess = cb.lessThan(root.get("id"), cursorId);

                        Predicate tieBreaker2 = cb.and(likeEqualCondition, createdLess);
                        Predicate tieBreaker3 = cb.and(likeEqualCondition, createdEqualCondition, idLess);

                        predicates.add(cb.or(likeLess, tieBreaker2, tieBreaker3));
                    }
                } else { // Default: createdAt
                    // Sort field hierarchy: createdAt -> id
                    Predicate createdEqualCondition = cb.equal(root.get("createdAt"), after);

                    if ("ASC".equalsIgnoreCase(direction)) {
                        Predicate createdGreater = cb.greaterThan(root.get("createdAt"), after);
                        Predicate idGreater = cb.greaterThan(root.get("id"), cursorId);

                        predicates.add(cb.or(createdGreater, cb.and(createdEqualCondition, idGreater)));
                    } else { // DESC
                        Predicate createdLess = cb.lessThan(root.get("createdAt"), after);
                        Predicate idLess = cb.lessThan(root.get("id"), cursorId);

                        predicates.add(cb.or(createdLess, cb.and(createdEqualCondition, idLess)));
                    }
                }
            }

            // Apply Sort ordering to the query
            if ("likeCount".equals(orderBy)) {
                if ("ASC".equalsIgnoreCase(direction)) {
                    query.orderBy(
                            cb.asc(root.get("likeCount")),
                            cb.asc(root.get("createdAt")),
                            cb.asc(root.get("id"))
                    );
                } else {
                    query.orderBy(
                            cb.desc(root.get("likeCount")),
                            cb.desc(root.get("createdAt")),
                            cb.desc(root.get("id"))
                    );
                }
            } else { // Default: createdAt
                if ("ASC".equalsIgnoreCase(direction)) {
                    query.orderBy(
                            cb.asc(root.get("createdAt")),
                            cb.asc(root.get("id"))
                    );
                } else {
                    query.orderBy(
                            cb.desc(root.get("createdAt")),
                            cb.desc(root.get("id"))
                    );
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
