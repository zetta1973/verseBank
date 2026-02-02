package com.versebank.accounts.domain.valueobjects;

public enum AccountType {
    CHECKING("Checking"),
    SAVINGS("Savings"),
    BUSINESS("Business");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

    public String getDescription() { return description; }

    public boolean allowsOverdraft() {
        return this == CHECKING || this == BUSINESS;
    }

    /**
     * Calcula la comisión por transferencia basada en el tipo de cuenta
     */
    public java.math.BigDecimal calculateTransferFee(java.math.BigDecimal amount) {
        if (amount == null) {
            return java.math.BigDecimal.ZERO;
        }
        
        // Cuentas Business tienen 1% de comisión con máximo $10
        if (this == BUSINESS) {
            java.math.BigDecimal fee = amount.multiply(java.math.BigDecimal.valueOf(0.01));
            return fee.min(java.math.BigDecimal.valueOf(10));
        }
        
        // Otros tipos no tienen comisión
        return java.math.BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "AccountType{" + name() + " - " + description + "}";
    }
}