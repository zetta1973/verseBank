package com.versebank.accounts.infrastructure.persistence;

import com.versebank.accounts.application.port.out.AccountRepository;
import com.versebank.accounts.domain.Account;
import com.versebank.accounts.domain.events.DomainEvent;
import com.versebank.accounts.infrastructure.persistence.mappers.AccountMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el puerto AccountRepository usando JPA
 */
public class AccountRepositoryAdapter implements AccountRepository {
    
    private final AccountJpaRepository jpaRepository;

    public AccountRepositoryAdapter(AccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Account> findById(String accountId) {
        return jpaRepository.findById(accountId)
                .map(AccountMapper::toDomain);
    }

    @Override
    public Account save(Account account) {
        Optional<AccountJpaEntity> existingEntity = jpaRepository.findById(account.getId().getValue());
        
        if (existingEntity.isPresent()) {
            // Update existing account
            AccountJpaEntity jpaEntity = existingEntity.get();
            AccountMapper.updateJpaFromDomain(account, jpaEntity);
            AccountJpaEntity savedEntity = jpaRepository.save(jpaEntity);
            return AccountMapper.toDomain(savedEntity);
        } else {
            // Create new account
            AccountJpaEntity jpaEntity = AccountMapper.toJpa(account);
            AccountJpaEntity savedEntity = jpaRepository.save(jpaEntity);
            return AccountMapper.toDomain(savedEntity);
        }
    }

    @Override
    public void deleteById(String accountId) {
        jpaRepository.deleteById(accountId);
    }

    @Override
    public boolean existsById(String accountId) {
        return jpaRepository.existsById(accountId);
    }

    @Override
    public List<Account> findByCustomerId(String customerId) {
        return jpaRepository.findByCustomerId(customerId)
                .stream()
                .map(AccountMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void saveDomainEvent(DomainEvent event) {
        // Implementation would persist domain events
        // For now, this is a placeholder
        System.out.println("Domain Event saved: " + event.getEventType() + " - " + event.getEventId());
    }

    @Override
    public List<DomainEvent> getDomainEvents(String accountId) {
        // Implementation would retrieve domain events
        // For now, this is a placeholder
        return List.of();
    }
}