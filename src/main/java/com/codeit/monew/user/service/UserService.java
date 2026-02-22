package com.codeit.monew.user.service;

import com.codeit.monew.user.dto.RegisterRequest;
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
    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        // TODO: Password should be encrypted later using PasswordEncoder
        User user = User.builder()
                .email(request.email())
                .nickname(request.nickname())
                .password(request.password())
                .build();

        return userRepository.save(user);
    }
}
