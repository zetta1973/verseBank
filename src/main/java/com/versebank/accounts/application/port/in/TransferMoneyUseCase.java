package com.versebank.accounts.application.port.in;

import com.versebank.accounts.domain.Account;
import com.versebank.accounts.domain.valueobjects.Transaction;
import com.versebank.accounts.domain.exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (Use Case) que define la interfaz para las operaciones de transferencia de dinero
 */
public interface TransferMoneyUseCase {
    
    void transferMoney(String sourceAccountId, String targetAccountId, BigDecimal amount, String description) 
        throws InsufficientFundsException;
    
    void depositMoney(String accountId, BigDecimal amount, String description);
    
    void withdrawMoney(String accountId, BigDecimal amount, String description) 
        throws InsufficientFundsException;
    
    Optional<Account> getAccount(String accountId);
    
    List<Transaction> getAccountTransactions(String accountId);
    
    boolean hasSufficientBalance(String accountId, BigDecimal amount);
}