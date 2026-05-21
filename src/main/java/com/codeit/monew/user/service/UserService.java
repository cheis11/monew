package com.codeit.monew.user.service;

import com.codeit.monew.common.exception.NotFoundException;
import com.codeit.monew.common.exception.UnauthorizedException;
import com.codeit.monew.user.dto.UserDto;
import com.codeit.monew.user.dto.UserLoginRequest;
import com.codeit.monew.user.dto.UserRegisterRequest;
import com.codeit.monew.user.entity.User;
import com.codeit.monew.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto registerUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        // TODO: Password should be encrypted later using PasswordEncoder
        User user = User.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .password(request.getPassword())
                .build();

        User savedUser = userRepository.save(user);

        return UserDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public UserDto login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        // TODO: Password check should use PasswordEncoder later
        if (!user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .build();
    }
    @Transactional
    public UserDto updateUser(java.util.UUID userId, com.codeit.monew.user.dto.UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        user.update(request.getNickname());
        
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteUser(java.util.UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void hardDeleteUser(java.util.UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userRepository.hardDeleteById(userId);
    }

    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000) // Run every 1 minute
    @Transactional
    public void restoreDeletedUsers() {
        // Prototype requirement: restore users soft-deleted > 5 minutes ago
        java.time.LocalDateTime threshold = java.time.LocalDateTime.now().minusMinutes(5);
        int restoredCount = userRepository.restoreDeletedUsersBefore(threshold);
        if (restoredCount > 0) {
            // Logging can be added here
            System.out.println("Restored " + restoredCount + " users deleted before " + threshold);
        }
    }
}
