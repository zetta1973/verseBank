package com.versebank.accounts.infrastructure.persistence.mappers;

import com.versebank.accounts.domain.Account;
import com.versebank.accounts.domain.AccountId;
import com.versebank.accounts.domain.valueobjects.AccountType;
import com.versebank.accounts.domain.valueobjects.Balance;
import com.versebank.accounts.infrastructure.persistence.AccountJpaEntity;

import java.time.LocalDateTime;

public class AccountMapper {
    
    public static Account toDomain(AccountJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        AccountId accountId = AccountId.of(jpaEntity.getId());
        Balance balance = Balance.of(jpaEntity.getBalance());
        
        return new Account(
            accountId,
            jpaEntity.getCustomerId(),
            jpaEntity.getAccountType(),
            balance
        );
    }
    
    public static AccountJpaEntity toJpa(Account domain) {
        if (domain == null) {
            return null;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        return new AccountJpaEntity(
            domain.getId().getValue(),
            domain.getCustomerId(),
            domain.getAccountType(),
            domain.getBalance().getAmount(),
            now,
            now
        );
    }
    
    public static void updateJpaFromDomain(Account domain, AccountJpaEntity jpaEntity) {
        if (domain == null || jpaEntity == null) {
            return;
        }
        
        jpaEntity.setCustomerId(domain.getCustomerId());
        jpaEntity.setAccountType(domain.getAccountType());
        jpaEntity.setBalance(domain.getBalance().getAmount());
        jpaEntity.setUpdatedAt(LocalDateTime.now());
    }
}