package com.codeit.monew.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserUpdateRequest {

    @NotBlank
    @Size(min = 1, max = 20)
    private String nickname;

    public UserUpdateRequest(String nickname) {
        this.nickname = nickname;
    }
}
