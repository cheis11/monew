package com.codeit.monew.user.dto;

public record RegisterRequest(
        String email,
        String nickname,
        String password) {
}
