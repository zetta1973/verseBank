package com.versebank.accounts.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, String> {
    
    Optional<AccountJpaEntity> findById(String id);
    
    List<AccountJpaEntity> findByCustomerId(String customerId);
    
    boolean existsById(String id);
}