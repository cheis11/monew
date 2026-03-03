package com.codeit.monew.user.controller;

import com.codeit.monew.user.dto.UserDto;
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
        return ResponseEntity.ok(registeredUser);
    }
}
