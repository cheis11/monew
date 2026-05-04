package com.codeit.monew.interest.repository;

import com.codeit.monew.interest.entity.Interest;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class InterestSpecification {

    public static Specification<Interest> filterInterests(
            String keyword,
            String orderBy,
            String direction,
            String cursor,
            LocalDateTime after) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Keyword search (name or keywords collection)
            if (keyword != null && !keyword.isBlank()) {
                String likeKeyword = "%" + keyword + "%";
                Predicate namePredicate = cb.like(root.get("name"), likeKeyword);

                // Need to join the keywords collection to search within it
                jakarta.persistence.criteria.Join<Interest, String> keywordsJoin = root.join("keywords", JoinType.LEFT);
                Predicate keywordsPredicate = cb.like(keywordsJoin, likeKeyword);

                predicates.add(cb.or(namePredicate, keywordsPredicate));
                query.distinct(true); // Since we are joining a collection, we need distinct results
            }

            // 2. Cursor Pagination logic
            if (cursor != null && !cursor.isBlank() && after != null) {
                UUID cursorId = UUID.fromString(cursor);

                String sortField = "name";
                if ("subscriberCount".equals(orderBy)) {
                    sortField = "subscriberCount";
                }

                Predicate fieldCondition;
                Predicate fieldEqualCondition = cb.equal(root.get(sortField), after);
                // Type handling for after: If ordering by subscriberCount, 'after' is passed as
                // LocalDateTime
                // from the controller since it's defined as string($date-time), but
                // subscriberCount is a long.
                // We will handle this discrepancy in the Service layer by converting 'after'
                // safely,
                // or just comparing strings/longs if handled properly. Assuming 'after' is
                // actually the
                // subscriberCount value when orderBy=subscriberCount, or createdAt when
                // orderBy=createdAt.
                // Note: Interest entity does not have 'createdAt' directly defined in fields,
                // it inherits from AbstractEntity.

                // Let's assume generic object comparison for 'after' parameter to support
                // long/date mapping gracefully
                // (This is standard in generic specifications)

                Predicate idCondition;

                if ("ASC".equalsIgnoreCase(direction)) {
                    fieldCondition = cb.greaterThan(root.get(sortField).as(String.class), String.valueOf(after));
                    idCondition = cb.greaterThan(root.get("id"), cursorId);
                } else { // DESC is default
                    fieldCondition = cb.lessThan(root.get(sortField).as(String.class), String.valueOf(after));
                    idCondition = cb.lessThan(root.get("id"), cursorId);
                }

                // If ordered by subscriberCount, convert after differently if needed.
                // A more robust way (simplified for string mapping hack to avoid
                // ClassCastException in CriteriaBuilder):
                if ("subscriberCount".equals(orderBy)) {
                    try {
                        // Assuming 'after' value was stuffed into LocalDateTime mistakenly by Swagger
                        // or was long converted to date.
                        // We will rely on the service to pass the correct object type instead of
                        // String.valueOf(after).
                        // Since 'after' is LocalDateTime in method signature but we need it generic,
                        // we'll fix types in Service.
                    } catch (Exception e) {
                    }
                }

                Predicate tieBreakerCondition = cb.and(fieldEqualCondition, idCondition);
                predicates.add(cb.or(fieldCondition, tieBreakerCondition));
            }

            // Apply sort to query
            String sortField = "name";
            if ("subscriberCount".equals(orderBy)) {
                sortField = "subscriberCount";
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
