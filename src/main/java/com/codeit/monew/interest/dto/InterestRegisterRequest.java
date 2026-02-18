package com.codeit.monew.interest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterestRegisterRequest {

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @Size(min = 1, max = 10)
    private List<String> keywords;

    public InterestRegisterRequest(String name, List<String> keywords) {
        this.name = name;
        this.keywords = keywords;
    }
}
