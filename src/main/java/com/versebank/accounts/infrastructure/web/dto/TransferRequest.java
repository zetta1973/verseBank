package com.versebank.accounts.infrastructure.web.dto;

import java.math.BigDecimal;

public class TransferRequest {
    private String sourceAccountId;
    private String targetAccountId;
    private BigDecimal amount;
    private String description;

    protected TransferRequest() {}

    public TransferRequest(String sourceAccountId, String targetAccountId, BigDecimal amount, String description) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.description = description;
    }

    // Getters
    public String getSourceAccountId() { return sourceAccountId; }
    public String getTargetAccountId() { return targetAccountId; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }

    // Setters
    public void setSourceAccountId(String sourceAccountId) { this.sourceAccountId = sourceAccountId; }
    public void setTargetAccountId(String targetAccountId) { this.targetAccountId = targetAccountId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setDescription(String description) { this.description = description; }
}