package com.codeit.monew.interest.dto;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterestUpdateRequest {

    @Size(min = 1, max = 10)
    private List<String> keywords;

    public InterestUpdateRequest(List<String> keywords) {
        this.keywords = keywords;
    }
}
