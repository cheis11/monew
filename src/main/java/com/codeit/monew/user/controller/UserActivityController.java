package com.codeit.monew.user.controller;

import com.codeit.monew.user.dto.UserActivityDto;
import com.codeit.monew.user.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user-activities")
@RequiredArgsConstructor
public class UserActivityController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserActivityDto> getUserActivity(@PathVariable UUID userId) {
        UserActivityDto result = userService.getUserActivity(userId);
        return ResponseEntity.ok(result);
    }
}
