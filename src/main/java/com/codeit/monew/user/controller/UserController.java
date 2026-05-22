package com.codeit.monew.user.controller;

import com.codeit.monew.common.exception.UnauthorizedException;
import com.codeit.monew.user.dto.UserDto;
import com.codeit.monew.user.dto.UserLoginRequest;
import com.codeit.monew.user.dto.UserRegisterRequest;
import com.codeit.monew.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        UserDto registeredUser = userService.registerUser(request);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest request) {
        UserDto loggedInUser = userService.login(request);
        return ResponseEntity.ok(loggedInUser);
    }

    @org.springframework.web.bind.annotation.PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @org.springframework.web.bind.annotation.PathVariable java.util.UUID userId,
            @Valid @RequestBody com.codeit.monew.user.dto.UserUpdateRequest request,
            @org.springframework.web.bind.annotation.RequestHeader(value = "Monew-Request-User-ID", required = false) java.util.UUID headerUserId) {
        
        // Authorization check
        if (headerUserId == null || !headerUserId.equals(userId)) {
            throw new UnauthorizedException("권한이 없습니다.");
        }
        
        UserDto updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.ok(updatedUser);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @org.springframework.web.bind.annotation.PathVariable java.util.UUID userId,
            @org.springframework.web.bind.annotation.RequestHeader(value = "Monew-Request-User-ID", required = false) java.util.UUID headerUserId) {
        
        // Authorization check
        if (headerUserId == null || !headerUserId.equals(userId)) {
            throw new UnauthorizedException("권한이 없습니다.");
        }
        
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{userId}/hard")
    public ResponseEntity<Void> hardDeleteUser(
            @org.springframework.web.bind.annotation.PathVariable java.util.UUID userId,
            @org.springframework.web.bind.annotation.RequestHeader(value = "Monew-Request-User-ID", required = false) java.util.UUID headerUserId) {
        
        // Authorization check
        if (headerUserId == null || !headerUserId.equals(userId)) {
            throw new UnauthorizedException("권한이 없습니다.");
        }
        
        userService.hardDeleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
