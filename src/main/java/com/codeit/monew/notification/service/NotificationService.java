package com.codeit.monew.notification.service;

import com.codeit.monew.common.exception.NotFoundException;
import com.codeit.monew.notification.dto.CursorPageResponseNotificationDto;
import com.codeit.monew.notification.dto.NotificationDto;
import com.codeit.monew.notification.entity.Notification;
import com.codeit.monew.notification.repository.NotificationRepository;
import com.codeit.monew.notification.repository.NotificationSpecification;
import com.codeit.monew.user.entity.User;
import com.codeit.monew.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CursorPageResponseNotificationDto getNotifications(
            UUID userId,
            String cursor,
            LocalDateTime after,
            int limit) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        LocalDateTime afterTime = after;
        if (cursor != null && !cursor.isBlank()) {
            UUID cursorId = UUID.fromString(cursor);
            Notification cursorNotification = notificationRepository.findById(cursorId)
                    .orElseThrow(() -> new NotFoundException("Cursor notification not found"));
            if (afterTime == null) {
                afterTime = cursorNotification.getCreatedAt();
            }
        }

        Page<Notification> notificationPage = notificationRepository.findAll(
                NotificationSpecification.filterNotifications(userId, cursor, afterTime),
                PageRequest.of(0, limit + 1)
        );

        List<Notification> notifications = notificationPage.getContent();
        boolean hasNext = notifications.size() > limit;
        if (hasNext) {
            notifications = notifications.subList(0, limit);
        }

        List<NotificationDto> notificationDtos = notifications.stream()
                .map(n -> NotificationDto.builder()
                        .id(n.getId())
                        .createdAt(n.getCreatedAt())
                        .updatedAt(n.getUpdatedAt())
                        .confirmed(n.isConfirmed())
                        .userId(n.getUser().getId())
                        .content(n.getContent())
                        .resourceType(n.getResourceType())
                        .resourceId(n.getResourceId())
                        .build())
                .collect(Collectors.toList());

        String nextCursor = null;
        LocalDateTime nextAfter = null;

        if (!notificationDtos.isEmpty() && hasNext) {
            Notification lastNotification = notifications.get(notifications.size() - 1);
            nextCursor = lastNotification.getId().toString();
            nextAfter = lastNotification.getCreatedAt();
        }

        return CursorPageResponseNotificationDto.builder()
                .content(notificationDtos)
                .nextCursor(nextCursor)
                .nextAfter(nextAfter)
                .size(notificationDtos.size())
                .totalElements(notificationPage.getTotalElements())
                .hasNext(hasNext)
                .build();
    }

    @Transactional
    public void confirmNotification(UUID notificationId, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new NotFoundException("Notification not found");
        }

        notification.confirm();
    }

    @Transactional
    public void confirmAllNotifications(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        List<Notification> unconfirmed = notificationRepository.findByUserIdAndConfirmedFalse(userId);
        for (Notification notification : unconfirmed) {
            notification.confirm();
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteOldConfirmedNotifications() {
        LocalDateTime threshold = LocalDateTime.now().minusWeeks(1);
        notificationRepository.deleteConfirmedNotificationsOlderThan(threshold);
    }

    @Transactional
    public void createNotification(User user, String content, String resourceType, UUID resourceId) {
        Notification notification = Notification.builder()
                .user(user)
                .content(content)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .build();
        notificationRepository.save(notification);
    }
}
