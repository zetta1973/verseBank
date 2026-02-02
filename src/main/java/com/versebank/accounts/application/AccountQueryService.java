package com.versebank.accounts.application;

import com.versebank.accounts.application.port.in.AccountQueryPort;
import com.versebank.accounts.application.port.in.AccountSummary;
import com.versebank.accounts.application.port.out.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación que implementa los casos de uso para consultas de cuentas
 */
@Service
public class AccountQueryService implements AccountQueryPort {
    
    private final AccountRepository accountRepository;
    
    public AccountQueryService(AccountRepository accountRepository) {
        if (accountRepository == null) {
            throw new NullPointerException("AccountRepository cannot be null");
        }
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<AccountSummary> findByAccountId(String accountId) {
        return accountRepository.findById(accountId)
                .map(account -> new AccountSummary(
                        account.getId().getValue(),
                        account.getCustomerId(),
                        account.getAccountType().name(),
                        account.getBalance().getAmount()
                ));
    }

    @Override
    public List<AccountSummary> findAccountsByCustomerId(String customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
                .map(account -> new AccountSummary(
                        account.getId().getValue(),
                        account.getCustomerId(),
                        account.getAccountType().name(),
                        account.getBalance().getAmount()
                ))
                .collect(Collectors.toList());
    }
}