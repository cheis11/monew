package com.codeit.monew.interest.service;

import com.codeit.monew.common.exception.ConflictException;
import com.codeit.monew.common.exception.NotFoundException;
import com.codeit.monew.interest.dto.SubscriptionDto;
import com.codeit.monew.interest.entity.Interest;
import com.codeit.monew.interest.entity.Subscription;
import com.codeit.monew.interest.repository.InterestRepository;
import com.codeit.monew.interest.repository.SubscriptionRepository;
import com.codeit.monew.user.entity.User;
import com.codeit.monew.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final InterestRepository interestRepository;
    private final UserRepository userRepository;

    @Transactional
    public SubscriptionDto subscribeInterest(UUID interestId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new NotFoundException("관심사를 찾을 수 없습니다."));

        if (subscriptionRepository.existsByUserIdAndInterestId(userId, interestId)) {
            throw new ConflictException("이미 구독 중인 관심사입니다.");
        }

        interest.increaseSubscriberCount();
        interestRepository.save(interest); // Implicitly saved due to transactional, but good practice.

        Subscription subscription = Subscription.builder()
                .user(user)
                .interest(interest)
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        return SubscriptionDto.builder()
                .id(savedSubscription.getId())
                .interestId(interest.getId())
                .interestName(interest.getName())
                .interestKeywords(interest.getKeywords())
                .interestSubscriberCount(interest.getSubscriberCount())
                .createdAt(savedSubscription.getCreatedAt())
                .build();
    }
}
