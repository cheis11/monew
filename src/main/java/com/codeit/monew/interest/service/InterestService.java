package com.codeit.monew.interest.service;

import com.codeit.monew.common.exception.ConflictException;
import com.codeit.monew.interest.dto.InterestDto;
import com.codeit.monew.interest.dto.InterestRegisterRequest;
import com.codeit.monew.interest.entity.Interest;
import com.codeit.monew.interest.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;

    @Transactional
    public InterestDto registerInterest(InterestRegisterRequest request) {
        if (interestRepository.existsByName(request.getName())) {
            throw new ConflictException("해당 이름의 관심사가 이미 존재합니다.");
        }

        Interest interest = Interest.builder()
                .name(request.getName())
                .keywords(request.getKeywords())
                .build();

        Interest savedInterest = interestRepository.save(interest);

        return InterestDto.builder()
                .id(savedInterest.getId())
                .name(savedInterest.getName())
                .keywords(savedInterest.getKeywords())
                .subscriberCount(savedInterest.getSubscriberCount())
                .subscribedByMe(false) // Will be handled if user subscription mappings are added later
                .build();
    }
}
