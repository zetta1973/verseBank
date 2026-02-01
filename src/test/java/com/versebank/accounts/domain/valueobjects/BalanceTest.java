package com.versebank.accounts.domain.valueobjects;

import com.versebank.accounts.domain.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for Balance value object
 * 🔥 50-60% of total tests - Critical domain logic testing
 */
@DisplayName("Balance Value Object Tests")
class BalanceTest {
    
    @Test
    @DisplayName("Should create balance successfully")
    void shouldCreateBalanceSuccessfully() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(1000.50);
        
        // When
        Balance balance = new Balance(amount);
        
        // Then
        assertThat(balance).isNotNull();
        assertThat(balance.getAmount()).isEqualTo(amount);
    }
    
    @Test
    @DisplayName("Should create balance with factory method")
    void shouldCreateBalanceWithFactoryMethod() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(500);
        
        // When
        Balance balance = Balance.of(amount);
        
        // Then
        assertThat(balance).isNotNull();
        assertThat(balance.getAmount()).isEqualTo(amount);
    }
    
    @Test
    @DisplayName("Should create zero balance")
    void shouldCreateZeroBalance() {
        // When
        Balance zeroBalance = Balance.zero();
        
        // Then
        assertThat(zeroBalance).isNotNull();
        assertThat(zeroBalance.getAmount()).isEqualTo(BigDecimal.ZERO);
    }
    
    @Test
    @DisplayName("Should throw exception when amount is null")
    void shouldThrowExceptionWhenAmountIsNull() {
        // When & Then
        assertThatThrownBy(() -> new Balance(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Balance cannot be null or negative");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-0.01", "-1000"})
    void shouldThrowExceptionWhenAmountIsNegative(String negativeAmount) {
        // Given
        BigDecimal amount = new BigDecimal(negativeAmount);
        
        // When & Then
        assertThatThrownBy(() -> new Balance(amount))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Balance cannot be null or negative");
    }
    
    @Test
    @DisplayName("Should add balances")
    void shouldAddBalances() {
        // Given
        Balance balance1 = Balance.of(BigDecimal.valueOf(1000));
        Balance balance2 = Balance.of(BigDecimal.valueOf(500));
        Balance expectedSum = Balance.of(BigDecimal.valueOf(1500));
        
        // When
        Balance result = balance1.add(balance2);
        
        // Then
        assertThat(result).isEqualTo(expectedSum);
    }
    
    @Test
    @DisplayName("Should add multiple balances")
    void shouldAddMultipleBalances() {
        // Given
        Balance balance1 = Balance.of(BigDecimal.valueOf(100));
        Balance balance2 = Balance.of(BigDecimal.valueOf(200));
        Balance balance3 = Balance.of(BigDecimal.valueOf(300));
        Balance expectedSum = Balance.of(BigDecimal.valueOf(600));
        
        // When
        Balance result = balance1.add(balance2).add(balance3);
        
        // Then
        assertThat(result).isEqualTo(expectedSum);
    }
    
    @Test
    void shouldSubtractBalances() throws InsufficientFundsException {
        // Given
        Balance balance1 = Balance.of(BigDecimal.valueOf(1000));
        Balance balance2 = Balance.of(BigDecimal.valueOf(300));
        Balance expectedDifference = Balance.of(BigDecimal.valueOf(700));
        
        // When
        Balance result = balance1.subtract(balance2);
        
        // Then
        assertThat(result).isEqualTo(expectedDifference);
    }
    
    @Test
    void shouldSubtractEqualBalances() throws InsufficientFundsException {
        // Given
        Balance balance1 = Balance.of(BigDecimal.valueOf(500));
        Balance balance2 = Balance.of(BigDecimal.valueOf(500));
        Balance expectedDifference = Balance.zero();
        
        // When
        Balance result = balance1.subtract(balance2);
        
        // Then
        assertThat(result).isEqualTo(expectedDifference);
    }
    
    @Test
    void shouldThrowExceptionWhenSubtractingLargerBalance() throws Exception {
        // Given
        Balance balance1 = Balance.of(BigDecimal.valueOf(300));
        Balance balance2 = Balance.of(BigDecimal.valueOf(500));
        
        // When & Then
        assertThatThrownBy(() -> balance1.subtract(balance2))
            .isInstanceOf(com.versebank.accounts.domain.exceptions.InsufficientFundsException.class)
            .hasMessageContaining("Insufficient balance");
    }
    
    @Test
    void shouldCompareBalancesCorrectly() {
        // Given
        Balance balance100 = Balance.of(BigDecimal.valueOf(100));
        Balance balance200 = Balance.of(BigDecimal.valueOf(200));
        Balance balance100Again = Balance.of(BigDecimal.valueOf(100));
        
        // Then
        assertThat(balance100.getAmount()).isLessThan(balance200.getAmount());
        assertThat(balance200.getAmount()).isGreaterThan(balance100.getAmount());
        assertThat(balance100.getAmount()).isLessThanOrEqualTo(balance100Again.getAmount());
        assertThat(balance100.getAmount()).isGreaterThanOrEqualTo(balance100Again.getAmount());
        assertThat(balance100.getAmount()).isGreaterThanOrEqualTo(Balance.zero().getAmount());
        assertThat(balance200.getAmount()).isGreaterThanOrEqualTo(balance100.getAmount());
        assertThat(balance100.getAmount()).isLessThan(balance200.getAmount());
        assertThat(balance200.getAmount()).isGreaterThan(balance100.getAmount());
    }
    
    @Test
    void shouldCompareBalancesWithZero() {
        // Given
        Balance zeroBalance = Balance.zero();
        Balance positiveBalance = Balance.of(BigDecimal.valueOf(100));
        
        // Then
        assertThat(zeroBalance.getAmount()).isGreaterThanOrEqualTo(Balance.zero().getAmount());
        assertThat(positiveBalance.getAmount()).isGreaterThan(zeroBalance.getAmount());
        assertThat(positiveBalance.getAmount()).isGreaterThanOrEqualTo(zeroBalance.getAmount());
    }
    
    @Test
    void shouldImplementEqualsAndHashCode() {
        // Given
        Balance balance1 = Balance.of(BigDecimal.valueOf(1000.50));
        Balance balance2 = Balance.of(new BigDecimal("1000.50"));
        Balance balance3 = Balance.of(BigDecimal.valueOf(1000.51));
        
        // Then
        assertThat(balance1).isEqualTo(balance2);
        assertThat(balance1.hashCode()).isEqualTo(balance2.hashCode());
        assertThat(balance1).isNotEqualTo(balance3);
        assertThat(balance1.hashCode()).isNotEqualTo(balance3.hashCode());
    }
    
    @Test
    void shouldImplementToString() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(1234.56);
        Balance balance = Balance.of(amount);
        
        // When
        String result = balance.toString();
        
        // Then
        assertThat(result).contains("Balance{");
        assertThat(result).contains("1234.56");
        assertThat(result).contains("}");
    }
    
    @Test
    void shouldHandlePrecisionCorrectly() {
        // Given
        BigDecimal preciseAmount1 = new BigDecimal("123.456789");
        BigDecimal preciseAmount2 = new BigDecimal("123.456789");
        
        Balance balance1 = Balance.of(preciseAmount1);
        Balance balance2 = Balance.of(preciseAmount2);
        
        // When
        Balance sum = balance1.add(balance2);
        
        // Then
        assertThat(sum.getAmount()).isEqualByComparingTo(new BigDecimal("246.913578"));
    }
    
    @Test
    void shouldHandleLargeNumbers() {
        // Given
        Balance largeBalance1 = Balance.of(new BigDecimal("999999999.99"));
        Balance largeBalance2 = Balance.of(new BigDecimal("0.01"));
        Balance expectedSum = Balance.of(new BigDecimal("1000000000.00"));
        
        // When
        Balance sum = largeBalance1.add(largeBalance2);
        
        // Then
        assertThat(sum).isEqualTo(expectedSum);
    }
    
    @Test
    void shouldHandleSmallNumbers() {
        // Given
        Balance smallBalance1 = Balance.of(new BigDecimal("0.01"));
        Balance smallBalance2 = Balance.of(new BigDecimal("0.01"));
        Balance expectedSum = Balance.of(new BigDecimal("0.02"));
        
        // When
        Balance sum = smallBalance1.add(smallBalance2);
        
        // Then
        assertThat(sum).isEqualTo(expectedSum);
    }
    
    @Test
    void shouldSupportChainingOperations() throws InsufficientFundsException {
        // Given
        Balance balance = Balance.of(BigDecimal.valueOf(1000));
        
        // When
        Balance result = balance
            .add(Balance.of(BigDecimal.valueOf(200)))
            .subtract(Balance.of(BigDecimal.valueOf(150)))
            .add(Balance.of(BigDecimal.valueOf(50)));
        
        // Then
        Balance expected = Balance.of(BigDecimal.valueOf(1100));
        assertThat(result).isEqualTo(expected);
    }
    
    @Test
    void shouldValidateAmountWithTrailingZeros() {
        // Given
        BigDecimal amount1 = BigDecimal.valueOf(100);
        BigDecimal amount2 = new BigDecimal("100.00");
        BigDecimal amount3 = new BigDecimal("100.000");
        
        // When
        Balance balance1 = Balance.of(amount1);
        Balance balance2 = Balance.of(amount2);
        Balance balance3 = Balance.of(amount3);
        
        // Then
        assertThat(balance1).isEqualTo(balance2);
        assertThat(balance1).isEqualTo(balance3);
        assertThat(balance2).isEqualTo(balance3);
    }
}
