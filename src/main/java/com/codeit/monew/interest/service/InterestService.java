package com.codeit.monew.interest.service;

import com.codeit.monew.common.exception.ConflictException;
import com.codeit.monew.common.exception.NotFoundException;
import com.codeit.monew.interest.dto.CursorPageResponseInterestDto;
import com.codeit.monew.interest.dto.InterestDto;
import com.codeit.monew.interest.dto.InterestRegisterRequest;
import com.codeit.monew.interest.dto.InterestUpdateRequest;
import com.codeit.monew.interest.entity.Interest;
import com.codeit.monew.interest.repository.InterestRepository;
import com.codeit.monew.interest.repository.SubscriptionRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterestService {

        private final InterestRepository interestRepository;
        private final SubscriptionRepository subscriptionRepository;

        @Transactional
        public InterestDto registerInterest(InterestRegisterRequest request) {
                if (interestRepository.existsByName(request.getName())) {
                        throw new ConflictException("해당 이름의 관심사가 이미 존재합니다.");
                }

                java.util.List<String> allNames = interestRepository.findAllNames();
                for (String existingName : allNames) {
                        if (calculateSimilarity(request.getName(), existingName) >= 0.8) {
                                throw new ConflictException("80% 이상 유사한 이름의 관심사가 이미 존재합니다: " + existingName);
                        }
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

        @Transactional(readOnly = true)
        public CursorPageResponseInterestDto getInterests(
                        String keyword,
                        String orderBy,
                        String direction,
                        String cursor,
                        java.time.LocalDateTime after,
                        int limit,
                        java.util.UUID userId) {

                String validOrderBy = "subscriberCount".equals(orderBy) ? orderBy : "name";
                String validDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC";

                org.springframework.data.domain.Page<Interest> interestPage = interestRepository.findAll(
                                com.codeit.monew.interest.repository.InterestSpecification.filterInterests(keyword,
                                                validOrderBy,
                                                validDirection, cursor, after),
                                org.springframework.data.domain.PageRequest.of(0, limit + 1));

                java.util.List<Interest> interests = interestPage.getContent();
                boolean hasNext = interests.size() > limit;
                if (hasNext) {
                        interests = interests.subList(0, limit);
                }

                java.util.List<InterestDto> interestDtos = interests.stream()
                                .map(interest -> InterestDto.builder()
                                                .id(interest.getId())
                                                .name(interest.getName())
                                                .keywords(interest.getKeywords())
                                                .subscriberCount(interest.getSubscriberCount())
                                                .subscribedByMe(false) // Subscription check logic goes here
                                                .build())
                                .collect(java.util.stream.Collectors.toList());

                String nextCursor = null;
                java.time.LocalDateTime nextAfter = null;

                if (!interestDtos.isEmpty() && hasNext) {
                        Interest lastInterest = interests.get(interests.size() - 1);
                        nextCursor = lastInterest.getId().toString();
                        // Since 'after' is passed as LocalDateTime but we sort by name/subscriberCount,
                        // the API design is slightly ambiguous. We map nextAfter to created_at or null
                        // for now.
                        try {
                                nextAfter = lastInterest.getCreatedAt();
                        } catch (Exception e) {
                        }
                }

                return CursorPageResponseInterestDto.builder()
                                .content(interestDtos)
                                .nextCursor(nextCursor)
                                .nextAfter(nextAfter)
                                .size(interestDtos.size())
                                .totalElements(interestPage.getTotalElements())
                                .hasNext(hasNext)
                                .build();
        }

        @Transactional
        public InterestDto updateInterest(UUID interestId, InterestUpdateRequest request) {
                Interest interest = interestRepository.findById(interestId)
                                .orElseThrow(() -> new NotFoundException("관심사를 찾을 수 없습니다."));

                interest.updateKeywords(request.getKeywords());

                return InterestDto.builder()
                                .id(interest.getId())
                                .name(interest.getName())
                                .keywords(interest.getKeywords())
                                .subscriberCount(interest.getSubscriberCount())
                                .subscribedByMe(false)
                                .build();
        }

        @Transactional
        public void deleteInterest(UUID interestId) {
                Interest interest = interestRepository.findById(interestId)
                                .orElseThrow(() -> new NotFoundException("관심사를 찾을 수 없습니다."));

                subscriptionRepository.deleteByInterestId(interestId);
                interestRepository.delete(interest);
        }

        private double calculateSimilarity(String s1, String s2) {
                int maxLength = Math.max(s1.length(), s2.length());
                if (maxLength == 0) return 1.0;
                
                int[] costs = new int[s2.length() + 1];
                for (int i = 0; i <= s1.length(); i++) {
                        int lastValue = i;
                        for (int j = 0; j <= s2.length(); j++) {
                                if (i == 0) {
                                        costs[j] = j;
                                } else if (j > 0) {
                                        int newValue = costs[j - 1];
                                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                                                newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                                        }
                                        costs[j - 1] = lastValue;
                                        lastValue = newValue;
                                }
                        }
                        if (i > 0) {
                                costs[s2.length()] = lastValue;
                        }
                }
                int distance = costs[s2.length()];
                return 1.0 - ((double) distance / maxLength);
        }
}
