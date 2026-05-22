package com.codeit.monew.notification.controller;

import com.codeit.monew.notification.dto.CursorPageResponseNotificationDto;
import com.codeit.monew.notification.service.NotificationService;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<CursorPageResponseNotificationDto> getNotifications(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) String after,
            @RequestParam(defaultValue = "50") int limit,
            @RequestHeader("Monew-Request-User-ID") UUID userId) {

        LocalDateTime afterTime = parseDateTime(after);

        CursorPageResponseNotificationDto response = notificationService.getNotifications(
                userId, cursor, afterTime, limit);

        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<Void> confirmAllNotifications(
            @RequestHeader("Monew-Request-User-ID") UUID userId) {
        notificationService.confirmAllNotifications(userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{notificationId}")
    public ResponseEntity<Void> confirmNotification(
            @PathVariable UUID notificationId,
            @RequestHeader("Monew-Request-User-ID") UUID userId) {
        notificationService.confirmNotification(notificationId, userId);
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format: " + dateStr);
        }
    }
}
