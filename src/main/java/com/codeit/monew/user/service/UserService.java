package com.codeit.monew.user.service;

import com.codeit.monew.user.dto.UserDto;
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
}
