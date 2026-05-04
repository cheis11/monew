package com.codeit.monew.interest.repository;

import com.codeit.monew.interest.entity.Interest;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InterestRepository extends JpaRepository<Interest, UUID>, JpaSpecificationExecutor<Interest> {
    boolean existsByName(String name);
}
