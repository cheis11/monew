package com.codeit.monew.interest.repository;

import com.codeit.monew.interest.entity.Subscription;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);
    
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Subscription s WHERE s.user.id = :userId AND s.interest.id = :interestId")
    void deleteByUserIdAndInterestId(@org.springframework.data.repository.query.Param("userId") UUID userId, @org.springframework.data.repository.query.Param("interestId") UUID interestId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM Subscription s WHERE s.interest.id = :interestId")
    void deleteByInterestId(@org.springframework.data.repository.query.Param("interestId") UUID interestId);

    @org.springframework.data.jpa.repository.Query("SELECT s.interest FROM Subscription s WHERE s.user.id = :userId")
    java.util.List<com.codeit.monew.interest.entity.Interest> findInterestsByUserId(@org.springframework.data.repository.query.Param("userId") UUID userId);

    java.util.List<Subscription> findByInterestId(UUID interestId);
    java.util.List<Subscription> findByUserId(UUID userId);
}
