package com.versebank.accounts.domain;

import com.versebank.accounts.domain.valueobjects.Balance;
import com.versebank.accounts.domain.valueobjects.AccountType;
import com.versebank.accounts.domain.exceptions.InsufficientFundsException;

/**
 * Servicio de dominio que encapsula la lógica de negocio
 * relacionada con cuentas que no pertenece a una entidad específica
 */
public class AccountDomainService {
    
    /**
     * Valida si se puede crear una cuenta con los parámetros dados
     */
    public boolean canCreateAccount(AccountType accountType, Balance initialBalance) {
        if (accountType == null || initialBalance == null) {
            return false;
        }
        
        // Lógica de negocio: cuentas de ahorro requieren mínimo $100
        if (accountType == AccountType.SAVINGS) {
            return initialBalance.getAmount().compareTo(java.math.BigDecimal.valueOf(100)) >= 0;
        }
        
        // Otros tipos de cuenta no tienen restricciones mínimas
        return true;
    }
    
    /**
     * Calcula la comisión por transferencia basada en el tipo de cuenta
     */
    public java.math.BigDecimal calculateTransferFee(AccountType accountType, java.math.BigDecimal amount) {
        if (accountType == null || amount == null) {
            return java.math.BigDecimal.ZERO;
        }
        
        // Cuentas Business tienen 1% de comisión con máximo $10
        if (accountType == AccountType.BUSINESS) {
            java.math.BigDecimal fee = amount.multiply(java.math.BigDecimal.valueOf(0.01));
            return fee.min(java.math.BigDecimal.valueOf(10));
        }
        
        // Otros tipos no tienen comisión
        return java.math.BigDecimal.ZERO;
    }
    
    /**
     * Valida si una transferencia entre dos cuentas es válida
     */
    public boolean canTransferMoney(Account sourceAccount, Account targetAccount, java.math.BigDecimal amount) {
        if (sourceAccount == null || targetAccount == null || amount == null) {
            return false;
        }
        
        // No se puede transferir a la misma cuenta
        if (sourceAccount.getId().equals(targetAccount.getId())) {
            return false;
        }
        
        // Verificar que ambas cuentas están activas (en una implementación real)
        // Por ahora solo verificamos que tienen suficiente balance
        java.math.BigDecimal totalAmount = amount.add(calculateTransferFee(sourceAccount.getAccountType(), amount));
        return sourceAccount.hasSufficientBalance(totalAmount);
    }
    
    /**
     * Calcula el interés para una cuenta de ahorro
     */
    public java.math.BigDecimal calculateInterest(Account savingsAccount) {
        if (savingsAccount == null || 
            savingsAccount.getAccountType() != AccountType.SAVINGS) {
            return java.math.BigDecimal.ZERO;
        }
        
        // 2% de interés anual (simplificado)
        java.math.BigDecimal balance = savingsAccount.getBalance().getAmount();
        return balance.multiply(java.math.BigDecimal.valueOf(0.02));
    }
    
    /**
     * Aplica el límite de sobregiro según el tipo de cuenta
     */
    public boolean canApplyOverdraft(Account account, java.math.BigDecimal requestedAmount) {
        if (account == null || requestedAmount == null) {
            return false;
        }
        
        // Solo cuentas Checking y Business permiten sobregiro
        if (!account.getAccountType().allowsOverdraft()) {
            return false;
        }
        
        // Límite máximo de sobregiro: $1000
        return requestedAmount.compareTo(java.math.BigDecimal.valueOf(1000)) <= 0;
    }
}