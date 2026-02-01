package com.versebank.accounts.domain.valueobjects;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class Transaction {
    private final String transactionId;
    private final LocalDateTime timestamp;
    private final BigDecimal amount;
    private final String description;
    private final TransactionType type;

    private Transaction(String transactionId, LocalDateTime timestamp, BigDecimal amount, 
                      String description, TransactionType type) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("TransactionId cannot be null or empty");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("TransactionType cannot be null");
        }

        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.amount = amount;
        this.description = description;
        this.type = type;
    }

    public static Transaction create(BigDecimal amount, String description, TransactionType type) {
        return new Transaction(
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            amount,
            description,
            type
        );
    }

    public static Transaction withId(String transactionId, LocalDateTime timestamp, 
                                   BigDecimal amount, String description, TransactionType type) {
        return new Transaction(transactionId, timestamp, amount, description, type);
    }

    public String getTransactionId() { return transactionId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public TransactionType getType() { return type; }

    public boolean isDeposit() {
        return type == TransactionType.DEPOSIT;
    }

    public boolean isWithdrawal() {
        return type == TransactionType.WITHDRAWAL;
    }

    public boolean isTransfer() {
        return type == TransactionType.TRANSFER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + transactionId + '\'' +
                ", timestamp=" + timestamp +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", type=" + type +
                '}';
    }

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, FEE
    }
}