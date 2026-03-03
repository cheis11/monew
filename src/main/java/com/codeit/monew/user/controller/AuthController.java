package com.codeit.monew.user.controller;

import com.codeit.monew.user.dto.AuthRequest;
import com.codeit.monew.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // Placeholder for actual login logic, this will need Session or JWT
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        // Here we just acknowledge it for now.
        return ResponseEntity.ok("Login endpoint ready. Not fully implemented yet.");
    }
}
