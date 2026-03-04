package com.codeit.monew.article.repository;

import com.codeit.monew.article.entity.Article;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class ArticleSpecification {

    public static Specification<Article> filterArticles(
            String keyword,
            UUID interestId,
            String sourceIn,
            LocalDateTime publishDateFrom,
            LocalDateTime publishDateTo,
            String orderBy,
            String direction,
            String cursor,
            LocalDateTime after) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Keyword search (title or summary)
            if (keyword != null && !keyword.isBlank()) {
                String likeKeyword = "%" + keyword + "%";
                Predicate titlePredicate = cb.like(root.get("title"), likeKeyword);
                Predicate summaryPredicate = cb.like(root.get("summary"), likeKeyword);
                predicates.add(cb.or(titlePredicate, summaryPredicate));
            }

            // 2. Interest ID filter (requires join with Interest keywords if applicable,
            // but since interest and article
            // aren't directly linked through a foreign key in Article entity, we might need
            // a subquery or pass keywords directly.
            // For now, if interestId is provided, we assumes the caller wants articles
            // matching the interest's keywords.
            // This logic will be handled in the service layer by fetching the interest
            // keywords first and passing them
            // to this specification if needed. Alternatively, if we just need a placeholder
            // we can leave it here, but
            // since there is no direct relationship, we handle it in service.)

            // 3. Source filter
            if (sourceIn != null && !sourceIn.isBlank()) {
                predicates.add(cb.equal(root.get("source"), sourceIn));
            }

            // 4. Date range filter
            if (publishDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("publishDate"), publishDateFrom));
            }
            if (publishDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("publishDate"), publishDateTo));
            }

            // 5. Cursor Pagination logic
            // (ASC / DESC logic for the exact sort column + ID tiebreaker)
            if (cursor != null && !cursor.isBlank() && after != null) {
                UUID cursorId = UUID.fromString(cursor);

                String sortField = "publishDate";
                if ("commentCount".equals(orderBy)) {
                    // Assuming commentCount exists or we use publishDate as fallback since
                    // commentCount is not in Article entity directly
                    // According to Article.java, commentCount doesn't exist on Article, only
                    // viewCount does.
                    // If it's a virtual column, this gets complex. We will map commentCount to
                    // publishDate fallback or 0.
                } else if ("viewCount".equals(orderBy)) {
                    sortField = "viewCount";
                }

                // If DESC: (field < after) OR (field == after AND id < cursorId)
                // If ASC: (field > after) OR (field == after AND id > cursorId)

                Predicate fieldCondition;
                Predicate fieldEqualCondition = cb.equal(root.get(sortField), after);
                Predicate idCondition;

                if ("ASC".equalsIgnoreCase(direction)) {
                    fieldCondition = cb.greaterThan(root.get(sortField), after);
                    idCondition = cb.greaterThan(root.get("id"), cursorId);
                } else { // DESC is default
                    fieldCondition = cb.lessThan(root.get(sortField), after);
                    idCondition = cb.lessThan(root.get("id"), cursorId);
                }

                Predicate tieBreakerCondition = cb.and(fieldEqualCondition, idCondition);
                predicates.add(cb.or(fieldCondition, tieBreakerCondition));
            }

            // Apply sort to query
            String sortField = "publishDate";
            if ("viewCount".equals(orderBy)) {
                sortField = "viewCount";
            }

            if ("ASC".equalsIgnoreCase(direction)) {
                query.orderBy(cb.asc(root.get(sortField)), cb.asc(root.get("id")));
            } else {
                query.orderBy(cb.desc(root.get(sortField)), cb.desc(root.get("id")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
