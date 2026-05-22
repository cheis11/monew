package com.codeit.monew.notification.repository;

import com.codeit.monew.notification.entity.Notification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public class NotificationSpecification {

    public static Specification<Notification> filterNotifications(
            UUID userId,
            String cursor,
            LocalDateTime after) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. User ID filter
            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }

            // 2. Only unconfirmed notifications
            predicates.add(cb.equal(root.get("confirmed"), false));

            // 3. Cursor Pagination logic
            if (cursor != null && !cursor.isBlank() && after != null) {
                UUID cursorId = UUID.fromString(cursor);

                // Sort: createdAt DESC -> id DESC
                Predicate createdEqualCondition = cb.equal(root.get("createdAt"), after);
                Predicate createdLess = cb.lessThan(root.get("createdAt"), after);
                Predicate idLess = cb.lessThan(root.get("id"), cursorId);

                predicates.add(cb.or(createdLess, cb.and(createdEqualCondition, idLess)));
            }

            // 4. Sort ordering (newest first)
            query.orderBy(
                    cb.desc(root.get("createdAt")),
                    cb.desc(root.get("id"))
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
