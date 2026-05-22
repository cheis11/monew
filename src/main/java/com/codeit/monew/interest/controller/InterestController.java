package com.codeit.monew.interest.controller;

import com.codeit.monew.interest.dto.InterestDto;
import com.codeit.monew.interest.dto.InterestRegisterRequest;
import com.codeit.monew.interest.service.InterestService;
import com.codeit.monew.interest.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

    private final InterestService interestService;
    private final SubscriptionService subscriptionService;

    @PostMapping("/{interestId}/subscriptions")
    public ResponseEntity<com.codeit.monew.interest.dto.SubscriptionDto> subscribeInterest(
            @PathVariable java.util.UUID interestId,
            @RequestHeader(value = "Monew-Request-User-ID") java.util.UUID userId) {

        com.codeit.monew.interest.dto.SubscriptionDto result = subscriptionService.subscribeInterest(interestId,
                userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<InterestDto> registerInterest(@Valid @RequestBody InterestRegisterRequest request) {
        InterestDto registeredInterest = interestService.registerInterest(request);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(registeredInterest);
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

    @org.springframework.web.bind.annotation.DeleteMapping("/{interestId}/subscriptions")
    public ResponseEntity<Void> unsubscribeInterest(
            @PathVariable java.util.UUID interestId,
            @RequestHeader(value = "Monew-Request-User-ID") java.util.UUID userId) {
        
        subscriptionService.unsubscribeInterest(interestId, userId);
        return ResponseEntity.ok().build();
    }

    @org.springframework.web.bind.annotation.PatchMapping("/{interestId}")
    public ResponseEntity<InterestDto> updateInterest(
            @PathVariable java.util.UUID interestId,
            @Valid @RequestBody com.codeit.monew.interest.dto.InterestUpdateRequest request) {
        
        InterestDto updatedInterest = interestService.updateInterest(interestId, request);
        return ResponseEntity.ok(updatedInterest);
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{interestId}")
    public ResponseEntity<Void> deleteInterest(
            @PathVariable java.util.UUID interestId) {
        
        interestService.deleteInterest(interestId);
        return ResponseEntity.noContent().build();
    }
}
