package com.versebank.accounts.application.port.out;

import com.versebank.accounts.domain.Account;
import com.versebank.accounts.domain.events.DomainEvent;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para operaciones de persistencia de cuentas
 */
public interface AccountRepository {
    
    Optional<Account> findById(String accountId);
    
    Account save(Account account);
    
    void deleteById(String accountId);
    
    boolean existsById(String accountId);
    
    List<Account> findByCustomerId(String customerId);
    
    void saveDomainEvent(DomainEvent event);
    
    List<DomainEvent> getDomainEvents(String accountId);
}