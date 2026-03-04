package com.codeit.monew.interest.repository;

import com.codeit.monew.interest.entity.Subscription;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);
}
