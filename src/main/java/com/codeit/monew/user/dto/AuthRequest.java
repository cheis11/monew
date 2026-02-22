package com.codeit.monew.user.dto;

public record AuthRequest(
        String email,
        String password) {
}
