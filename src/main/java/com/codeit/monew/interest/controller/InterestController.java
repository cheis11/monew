package com.codeit.monew.interest.controller;

import com.codeit.monew.interest.dto.InterestDto;
import com.codeit.monew.interest.dto.InterestRegisterRequest;
import com.codeit.monew.interest.service.InterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

    private final InterestService interestService;

    @PostMapping
    public ResponseEntity<InterestDto> registerInterest(@Valid @RequestBody InterestRegisterRequest request) {
        InterestDto registeredInterest = interestService.registerInterest(request);
        return ResponseEntity.ok(registeredInterest);
    }

    @org.springframework.web.bind.annotation.GetMapping
    public ResponseEntity<com.codeit.monew.interest.dto.CursorPageResponseInterestDto> getInterests(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String keyword,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "name") String orderBy,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "ASC") String direction,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String cursor,
            @org.springframework.web.bind.annotation.RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime after,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "50") int limit,
            @org.springframework.web.bind.annotation.RequestHeader(value = "Monew-Request-User-ID", required = true) java.util.UUID userId) {

        com.codeit.monew.interest.dto.CursorPageResponseInterestDto response = interestService.getInterests(
                keyword, orderBy, direction, cursor, after, limit, userId);

        return ResponseEntity.ok(response);
    }
}
