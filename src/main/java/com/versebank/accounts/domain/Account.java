package com.versebank.accounts.domain;

import com.versebank.accounts.domain.valueobjects.AccountType;
import com.versebank.accounts.domain.valueobjects.Balance;
import com.versebank.accounts.domain.valueobjects.Transaction;
import com.versebank.accounts.domain.exceptions.InsufficientFundsException;
import com.versebank.accounts.domain.events.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Account {
    private final AccountId id;
    private final String customerId;
    private final AccountType accountType;
    private Balance balance;
    private final List<Transaction> transactions;
    private final List<DomainEvent> domainEvents;

    public Account(AccountId id, String customerId, AccountType accountType, Balance initialBalance) {
        if (id == null) throw new IllegalArgumentException("AccountId cannot be null");
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("CustomerId cannot be null or empty");
        }
        if (accountType == null) throw new IllegalArgumentException("AccountType cannot be null");
        if (initialBalance == null) throw new IllegalArgumentException("Initial balance cannot be null");

        this.id = id;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        this.domainEvents = new ArrayList<>();
    }

    public static Account create(String customerId, AccountType accountType, Balance initialBalance) {
        return new Account(AccountId.generate(), customerId, accountType, initialBalance);
    }

    public void deposit(Transaction transaction) {
        if (transaction == null) throw new IllegalArgumentException("Transaction cannot be null");
        
        this.balance = balance.add(Balance.of(transaction.getAmount()));
        this.transactions.add(transaction);
        
        // Emit domain event
        domainEvents.add(new MoneyDepositedEvent(transaction.getAmount(), getBalance().getAmount()));
    }

    public void withdraw(Transaction transaction) throws InsufficientFundsException {
        if (transaction == null) throw new IllegalArgumentException("Transaction cannot be null");
        
        Balance transactionBalance = Balance.of(transaction.getAmount());
        if (balance.isLessThan(transactionBalance)) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }
        
        this.balance = balance.subtract(transactionBalance);
        this.transactions.add(transaction);
        
        // Emit domain event
        domainEvents.add(new MoneyWithdrawnEvent(transaction.getAmount(), getBalance().getAmount()));
    }

    public void transfer(Account targetAccount, Transaction transaction) throws InsufficientFundsException {
        if (targetAccount == null) throw new IllegalArgumentException("Target account cannot be null");
        if (transaction == null) throw new IllegalArgumentException("Transaction cannot be null");
        
        // Withdraw from this account
        withdraw(transaction);
        
        // Deposit to target account using public method
        targetAccount.receiveTransfer(transaction);
    }

    public void receiveTransfer(Transaction transaction) {
        if (transaction == null) throw new IllegalArgumentException("Transaction cannot be null");
        
        this.balance = balance.add(Balance.of(transaction.getAmount()));
        this.transactions.add(transaction);
        
        // Emit domain event for received money
        domainEvents.add(new MoneyReceivedEvent(transaction.getAmount(), getBalance().getAmount()));
    }

    public boolean hasSufficientBalance(java.math.BigDecimal amount) {
        return balance.isGreaterThanOrEqual(Balance.of(amount));
    }

    /**
     * Calcula la comisión por transferencia basada en el tipo de cuenta
     */
    public java.math.BigDecimal calculateTransferFee() {
        return accountType.calculateTransferFee(balance.getAmount());
    }

    /**
     * Valida si se puede transferir a otra cuenta
     */
    public boolean canTransferTo(Account targetAccount, java.math.BigDecimal amount) {
        if (targetAccount == null || amount == null) {
            return false;
        }
        
        // No se puede transferir a la misma cuenta
        if (this.id.equals(targetAccount.getId())) {
            return false;
        }
        
        // Verificar saldo suficiente incluyendo comisión
        java.math.BigDecimal totalAmount = amount.add(calculateTransferFee());
        return hasSufficientBalance(totalAmount);
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    // Getters
    public AccountId getId() { return id; }
    public String getCustomerId() { return customerId; }
    public AccountType getAccountType() { return accountType; }
    public Balance getBalance() { return balance; }

    // Inner classes for domain events
    private static class MoneyDepositedEvent extends DomainEvent {
        private final java.math.BigDecimal amount;
        private final java.math.BigDecimal newBalance;

        public MoneyDepositedEvent(java.math.BigDecimal amount, java.math.BigDecimal newBalance) {
            super("MoneyDeposited");
            this.amount = amount;
            this.newBalance = newBalance;
        }

        public java.math.BigDecimal getAmount() { return amount; }
        public java.math.BigDecimal getNewBalance() { return newBalance; }
    }

    private static class MoneyWithdrawnEvent extends DomainEvent {
        private final java.math.BigDecimal amount;
        private final java.math.BigDecimal newBalance;

        public MoneyWithdrawnEvent(java.math.BigDecimal amount, java.math.BigDecimal newBalance) {
            super("MoneyWithdrawn");
            this.amount = amount;
            this.newBalance = newBalance;
        }

        public java.math.BigDecimal getAmount() { return amount; }
        public java.math.BigDecimal getNewBalance() { return newBalance; }
    }

    private static class MoneyReceivedEvent extends DomainEvent {
        private final java.math.BigDecimal amount;
        private final java.math.BigDecimal newBalance;

        public MoneyReceivedEvent(java.math.BigDecimal amount, java.math.BigDecimal newBalance) {
            super("MoneyReceived");
            this.amount = amount;
            this.newBalance = newBalance;
        }

        public java.math.BigDecimal getAmount() { return amount; }
        public java.math.BigDecimal getNewBalance() { return newBalance; }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}