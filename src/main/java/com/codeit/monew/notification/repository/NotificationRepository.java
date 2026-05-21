package com.codeit.monew.notification.repository;

import com.codeit.monew.notification.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID>, JpaSpecificationExecutor<Notification> {
    List<Notification> findByUserIdAndConfirmedFalse(UUID userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Notification n WHERE n.confirmed = true AND n.updatedAt < :threshold")
    int deleteConfirmedNotificationsOlderThan(@Param("threshold") LocalDateTime threshold);
}
