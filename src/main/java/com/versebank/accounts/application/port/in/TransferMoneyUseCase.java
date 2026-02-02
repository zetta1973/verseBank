package com.versebank.accounts.application.port.in;

import com.versebank.accounts.domain.exceptions.InsufficientFundsException;

import java.math.BigDecimal;

/**
 * Puerto de entrada (Use Case) que define la interfaz para las operaciones de transferencia de dinero
 */
public interface TransferMoneyUseCase {
    
    void transferMoney(String sourceAccountId, String targetAccountId, BigDecimal amount, String description) 
        throws InsufficientFundsException;
    
    void depositMoney(String accountId, BigDecimal amount, String description);
    
    void withdrawMoney(String accountId, BigDecimal amount, String description) 
        throws InsufficientFundsException;
        
    boolean hasSufficientBalance(String accountId, BigDecimal amount);
}
