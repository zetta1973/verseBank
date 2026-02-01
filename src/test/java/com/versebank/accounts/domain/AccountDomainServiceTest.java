package com.versebank.accounts.domain;

import com.versebank.accounts.domain.valueobjects.AccountType;
import com.versebank.accounts.domain.valueobjects.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static com.versebank.accounts.domain.valueobjects.AccountType.*;

/**
 * Test suite for AccountDomainService
 * 🔥 50-60% of total tests - Critical domain service testing
 */
@DisplayName("AccountDomainService Tests")
class AccountDomainServiceTest {
    
    private AccountDomainService accountDomainService;
    private Account checkingAccount;
    private Account savingsAccount;
    private Account businessAccount;
    
    @BeforeEach
    void setUp() {
        accountDomainService = new AccountDomainService();
        
        // Setup test accounts
        checkingAccount = Account.create("customer-123", CHECKING, Balance.of(BigDecimal.valueOf(1000)));
        savingsAccount = Account.create("customer-456", SAVINGS, Balance.of(BigDecimal.valueOf(500)));
        businessAccount = Account.create("customer-789", BUSINESS, Balance.of(BigDecimal.valueOf(2000)));
    }
    
    @Test
    @DisplayName("Should create account with valid parameters")
    void shouldCreateAccountWithValidParameters() {
        // Given
        Balance initialBalance = Balance.of(BigDecimal.valueOf(200));
        AccountType accountType = CHECKING;
        
        // When
        boolean canCreate = accountDomainService.canCreateAccount(accountType, initialBalance);
        
        // Then
        assertThat(canCreate).isTrue();
    }
    
    @Test
    @DisplayName("Should allow savings account with minimum balance")
    void shouldAllowSavingsAccountWithMinimumBalance() {
        // Given
        Balance minimumBalance = Balance.of(BigDecimal.valueOf(100));
        AccountType savingsType = SAVINGS;
        
        // When
        boolean canCreate = accountDomainService.canCreateAccount(savingsType, minimumBalance);
        
        // Then
        assertThat(canCreate).isTrue();
    }
    
    @Test
    @DisplayName("Should reject savings account with below minimum balance")
    void shouldRejectSavingsAccountWithBelowMinimumBalance() {
        // Given
        Balance belowMinimumBalance = Balance.of(BigDecimal.valueOf(99.99));
        AccountType savingsType = SAVINGS;
        
        // When
        boolean canCreate = accountDomainService.canCreateAccount(savingsType, belowMinimumBalance);
        
        // Then
        assertThat(canCreate).isFalse();
    }
    
    @Test
    @DisplayName("Should reject savings account with zero balance")
    void shouldRejectSavingsAccountWithZeroBalance() {
        // Given
        Balance zeroBalance = Balance.zero();
        AccountType savingsType = SAVINGS;
        
        // When
        boolean canCreate = accountDomainService.canCreateAccount(savingsType, zeroBalance);
        
        // Then
        assertThat(canCreate).isFalse();
    }
    
    @Test
    @DisplayName("Should allow business account without minimum balance")
    void shouldAllowBusinessAccountWithoutMinimumBalance() {
        // Given
        Balance smallBalance = Balance.of(BigDecimal.valueOf(10));
        AccountType businessType = BUSINESS;
        
        // When
        boolean canCreate = accountDomainService.canCreateAccount(businessType, smallBalance);
        
        // Then
        assertThat(canCreate).isTrue();
    }
    
    @Test
    @DisplayName("Should reject account with null type")
    void shouldRejectAccountWithNullType() {
        // Given
        Balance balance = Balance.of(BigDecimal.valueOf(500));
        
        // When
        boolean canCreate = accountDomainService.canCreateAccount(null, balance);
        
        // Then
        assertThat(canCreate).isFalse();
    }
    
    @Test
    void shouldRejectAccountWithNullBalance() {
        // Given
        AccountType accountType = CHECKING;
        
        // When
        boolean canCreate = accountDomainService.canCreateAccount(accountType, null);
        
        // Then
        assertThat(canCreate).isFalse();
    }
    
    @Test
    void shouldCalculateZeroFeeForCheckingAccount() {
        // Given
        AccountType checkingType = CHECKING;
        BigDecimal amount = BigDecimal.valueOf(1000);
        
        // When
        BigDecimal fee = accountDomainService.calculateTransferFee(checkingType, amount);
        
        // Then
        assertThat(fee).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void shouldCalculateZeroFeeForSavingsAccount() {
        // Given
        AccountType savingsType = SAVINGS;
        BigDecimal amount = BigDecimal.valueOf(500);
        
        // When
        BigDecimal fee = accountDomainService.calculateTransferFee(savingsType, amount);
        
        // Then
        assertThat(fee).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void shouldCalculateOnePercentFeeForBusinessAccount() {
        // Given
        AccountType businessType = BUSINESS;
        BigDecimal amount = BigDecimal.valueOf(1000);
        BigDecimal expectedFee = BigDecimal.valueOf(10); // 1% of 1000
        
        // When
        BigDecimal fee = accountDomainService.calculateTransferFee(businessType, amount);
        
        // Then
        assertThat(fee).isEqualByComparingTo(expectedFee);
    }
    
    @Test
    void shouldCapBusinessFeeAtMaximum() {
        // Given
        AccountType businessType = BUSINESS;
        BigDecimal amount = BigDecimal.valueOf(2000);
        BigDecimal onePercentFee = BigDecimal.valueOf(20); // 1% of 2000
        BigDecimal expectedFee = BigDecimal.valueOf(10); // Maximum allowed
        
        // When
        BigDecimal fee = accountDomainService.calculateTransferFee(businessType, amount);
        
        // Then
        assertThat(fee).isEqualByComparingTo(expectedFee);
        assertThat(onePercentFee.compareTo(expectedFee) > 0).isTrue(); // One percent would be higher
    }
    
    @Test
    void shouldCalculateFeeForLargeBusinessAmount() {
        // Given
        AccountType businessType = BUSINESS;
        BigDecimal amount = BigDecimal.valueOf(10000);
        BigDecimal expectedFee = BigDecimal.valueOf(10); // Capped at maximum
        
        // When
        BigDecimal fee = accountDomainService.calculateTransferFee(businessType, amount);
        
        // Then
        assertThat(fee).isEqualByComparingTo(expectedFee);
    }
    
    @Test
    void shouldReturnZeroFeeForNullAccountType() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(1000);
        
        // When
        BigDecimal fee = accountDomainService.calculateTransferFee(null, amount);
        
        // Then
        assertThat(fee).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void shouldReturnZeroFeeForNullAmount() {
        // Given
        AccountType accountType = BUSINESS;
        
        // When
        BigDecimal fee = accountDomainService.calculateTransferFee(accountType, null);
        
        // Then
        assertThat(fee).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void shouldValidateSuccessfulTransfer() {
        // Given
        Account sourceAccount = Account.create("customer-1", CHECKING, Balance.of(BigDecimal.valueOf(1000)));
        Account targetAccount = Account.create("customer-2", CHECKING, Balance.of(BigDecimal.valueOf(500)));
        BigDecimal transferAmount = BigDecimal.valueOf(200);
        
        // When
        boolean canTransfer = accountDomainService.canTransferMoney(sourceAccount, targetAccount, transferAmount);
        
        // Then
        assertThat(canTransfer).isTrue();
    }
    
    @Test
    void shouldRejectTransferToSameAccount() {
        // Given
        BigDecimal transferAmount = BigDecimal.valueOf(100);
        
        // When
        boolean canTransfer = accountDomainService.canTransferMoney(checkingAccount, checkingAccount, transferAmount);
        
        // Then
        assertThat(canTransfer).isFalse();
    }
    
    @Test
    void shouldRejectTransferWithInsufficientBalance() {
        // Given
        Account sourceAccount = Account.create("customer-1", CHECKING, Balance.of(BigDecimal.valueOf(100)));
        Account targetAccount = Account.create("customer-2", CHECKING, Balance.of(BigDecimal.valueOf(500)));
        BigDecimal transferAmount = BigDecimal.valueOf(200); // More than source balance
        
        // When
        boolean canTransfer = accountDomainService.canTransferMoney(sourceAccount, targetAccount, transferAmount);
        
        // Then
        assertThat(canTransfer).isFalse();
    }
    
    @Test
    void shouldRejectTransferWithNullSourceAccount() {
        // Given
        Account targetAccount = Account.create("customer-2", CHECKING, Balance.of(BigDecimal.valueOf(500)));
        BigDecimal transferAmount = BigDecimal.valueOf(100);
        
        // When
        boolean canTransfer = accountDomainService.canTransferMoney(null, targetAccount, transferAmount);
        
        // Then
        assertThat(canTransfer).isFalse();
    }
    
    @Test
    void shouldRejectTransferWithNullTargetAccount() {
        // Given
        Account sourceAccount = Account.create("customer-1", CHECKING, Balance.of(BigDecimal.valueOf(1000)));
        BigDecimal transferAmount = BigDecimal.valueOf(100);
        
        // When
        boolean canTransfer = accountDomainService.canTransferMoney(sourceAccount, null, transferAmount);
        
        // Then
        assertThat(canTransfer).isFalse();
    }
    
    @Test
    void shouldRejectTransferWithNullAmount() {
        // Given
        
        // When
        boolean canTransfer = accountDomainService.canTransferMoney(checkingAccount, savingsAccount, null);
        
        // Then
        assertThat(canTransfer).isFalse();
    }
    
    @Test
    void shouldCalculateInterestForSavingsAccount() {
        // Given
        Balance savingsBalance = Balance.of(BigDecimal.valueOf(1000));
        Account savingsAccountWithBalance = Account.create("customer-456", SAVINGS, savingsBalance);
        BigDecimal expectedInterest = BigDecimal.valueOf(20); // 2% of 1000
        
        // When
        BigDecimal interest = accountDomainService.calculateInterest(savingsAccountWithBalance);
        
        // Then
        assertThat(interest).isEqualByComparingTo(expectedInterest);
    }
    
    @Test
    void shouldCalculateInterestForLargeSavingsBalance() {
        // Given
        Balance largeBalance = Balance.of(BigDecimal.valueOf(10000));
        Account largeSavingsAccount = Account.create("customer-456", SAVINGS, largeBalance);
        BigDecimal expectedInterest = BigDecimal.valueOf(200); // 2% of 10000
        
        // When
        BigDecimal interest = accountDomainService.calculateInterest(largeSavingsAccount);
        
        // Then
        assertThat(interest).isEqualByComparingTo(expectedInterest);
    }
    
    @Test
    void shouldReturnZeroInterestForNonSavingsAccount() {
        // When
        BigDecimal interest = accountDomainService.calculateInterest(checkingAccount);
        
        // Then
        assertThat(interest).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void shouldReturnZeroInterestForNullAccount() {
        // When
        BigDecimal interest = accountDomainService.calculateInterest(null);
        
        // Then
        assertThat(interest).isEqualByComparingTo(BigDecimal.ZERO);
    }
    
    @Test
    void shouldAllowOverdraftForCheckingAccount() {
        // Given
        BigDecimal requestedAmount = BigDecimal.valueOf(500);
        
        // When
        boolean canApplyOverdraft = accountDomainService.canApplyOverdraft(checkingAccount, requestedAmount);
        
        // Then
        assertThat(canApplyOverdraft).isTrue();
    }
    
    @Test
    void shouldAllowOverdraftForBusinessAccount() {
        // Given
        BigDecimal requestedAmount = BigDecimal.valueOf(800);
        
        // When
        boolean canApplyOverdraft = accountDomainService.canApplyOverdraft(businessAccount, requestedAmount);
        
        // Then
        assertThat(canApplyOverdraft).isTrue();
    }
    
    @Test
    void shouldRejectOverdraftForSavingsAccount() {
        // Given
        BigDecimal requestedAmount = BigDecimal.valueOf(200);
        
        // When
        boolean canApplyOverdraft = accountDomainService.canApplyOverdraft(savingsAccount, requestedAmount);
        
        // Then
        assertThat(canApplyOverdraft).isFalse();
    }
    
    @Test
    void shouldRejectOverdraftBeyondLimit() {
        // Given
        BigDecimal beyondLimitAmount = BigDecimal.valueOf(1500); // Over $1000 limit
        
        // When
        boolean canApplyOverdraft = accountDomainService.canApplyOverdraft(checkingAccount, beyondLimitAmount);
        
        // Then
        assertThat(canApplyOverdraft).isFalse();
    }
    
    @Test
    void shouldAllowOverdraftAtExactLimit() {
        // Given
        BigDecimal exactLimitAmount = BigDecimal.valueOf(1000);
        
        // When
        boolean canApplyOverdraft = accountDomainService.canApplyOverdraft(checkingAccount, exactLimitAmount);
        
        // Then
        assertThat(canApplyOverdraft).isTrue();
    }
    
    @Test
    void shouldRejectOverdraftWithNullAccount() {
        // Given
        BigDecimal amount = BigDecimal.valueOf(500);
        
        // When
        boolean canApplyOverdraft = accountDomainService.canApplyOverdraft(null, amount);
        
        // Then
        assertThat(canApplyOverdraft).isFalse();
    }
    
    @Test
    void shouldRejectOverdraftWithNullAmount() {
        // When
        boolean canApplyOverdraft = accountDomainService.canApplyOverdraft(checkingAccount, null);
        
        // Then
        assertThat(canApplyOverdraft).isFalse();
    }
    
    @Test
    void shouldHandleComplexBusinessFeeCalculation() {
        // Test various amounts and verify business fee logic
        
        // Small amount - should apply 1%
        assertThat(accountDomainService.calculateTransferFee(BUSINESS, BigDecimal.valueOf(100))).isEqualByComparingTo(BigDecimal.valueOf(1));
        assertThat(accountDomainService.calculateTransferFee(BUSINESS, BigDecimal.valueOf(500))).isEqualByComparingTo(BigDecimal.valueOf(5));
        assertThat(accountDomainService.calculateTransferFee(BUSINESS, BigDecimal.valueOf(2000))).isEqualByComparingTo(BigDecimal.valueOf(10));
        assertThat(accountDomainService.calculateTransferFee(BUSINESS, BigDecimal.valueOf(50000))).isEqualByComparingTo(BigDecimal.valueOf(10));
    }
}
