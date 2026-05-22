package com.codeit.monew.user.repository;

import com.codeit.monew.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "UPDATE users SET deleted_at = NULL WHERE deleted_at IS NOT NULL AND deleted_at < :threshold", nativeQuery = true)
    int restoreDeletedUsersBefore(@org.springframework.data.repository.query.Param("threshold") java.time.LocalDateTime threshold);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "DELETE FROM users WHERE id = :userId", nativeQuery = true)
    void hardDeleteById(@org.springframework.data.repository.query.Param("userId") UUID userId);
}
