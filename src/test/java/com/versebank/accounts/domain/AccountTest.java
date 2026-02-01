package com.versebank.accounts.domain;

import com.versebank.accounts.domain.valueobjects.AccountType;
import com.versebank.accounts.domain.valueobjects.Balance;
import com.versebank.accounts.domain.valueobjects.Transaction;
import com.versebank.accounts.domain.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static com.versebank.accounts.domain.valueobjects.AccountType.*;
import static com.versebank.accounts.domain.valueobjects.Transaction.TransactionType.*;

/**
 * Test suite for Account domain entity
 * 🔥 50-60% of total tests - Critical business logic testing
 */
class AccountTest {
    
    private AccountId accountId;
    private String customerId;
    private AccountType accountType;
    private Balance initialBalance;
    
    @BeforeEach
    void setUp() {
        accountId = AccountId.generate();
        customerId = "customer-123";
        accountType = CHECKING;
        initialBalance = Balance.of(BigDecimal.valueOf(1000));
    }
    
    @Test
    void shouldCreateAccountSuccessfully() {
        // Given
        AccountId id = AccountId.generate();
        String customerId = "customer-456";
        AccountType type = SAVINGS;
        Balance balance = Balance.of(BigDecimal.valueOf(500));
        
        // When
        Account account = new Account(id, customerId, type, balance);
        
        // Then
        assertThat(account.getId()).isNotNull();
        assertThat(account.getCustomerId()).isEqualTo(customerId);
        assertThat(account.getAccountType()).isEqualTo(type);
        assertThat(account.getBalance()).isEqualTo(balance);
        assertThat(account.getTransactions()).isEmpty();
    }
    
    @Test
    void shouldCreateAccountWithFactoryMethod() {
        // When
        Account account = Account.create(customerId, accountType, initialBalance);
        
        // Then
        assertThat(account.getId()).isNotNull();
        assertThat(account.getCustomerId()).isEqualTo(customerId);
        assertThat(account.getAccountType()).isEqualTo(accountType);
        assertThat(account.getBalance()).isEqualTo(initialBalance);
    }
    
    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        // When & Then
        assertThatThrownBy(() -> 
            new Account(null, customerId, accountType, initialBalance)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("AccountId cannot be null");
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerIdIsNull() {
        // When & Then
        assertThatThrownBy(() -> 
            new Account(accountId, null, accountType, initialBalance)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("CustomerId cannot be null or empty");
    }
    
    @Test
    void shouldThrowExceptionWhenCustomerIdIsEmpty() {
        // When & Then
        assertThatThrownBy(() -> 
            new Account(accountId, "", accountType, initialBalance)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("CustomerId cannot be null or empty");
    }
    
    @Test
    void shouldThrowExceptionWhenAccountTypeIsNull() {
        // When & Then
        assertThatThrownBy(() -> 
            new Account(accountId, customerId, null, initialBalance)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("AccountType cannot be null");
    }
    
    @Test
    void shouldThrowExceptionWhenInitialBalanceIsNull() {
        // When & Then
        assertThatThrownBy(() -> 
            new Account(accountId, customerId, accountType, null)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessage("Initial balance cannot be null");
    }
    
    @Test
    void shouldDepositMoneySuccessfully() {
        // Given
        Account account = new Account(accountId, customerId, accountType, initialBalance);
        Transaction deposit = Transaction.create(BigDecimal.valueOf(200), "Cash deposit", DEPOSIT);
        Balance expectedNewBalance = initialBalance.add(Balance.of(BigDecimal.valueOf(200)));
        
        // When
        account.deposit(deposit);
        
        // Then
        assertThat(account.getBalance()).isEqualTo(expectedNewBalance);
        assertThat(account.getTransactions()).hasSize(1);
        assertThat(account.getTransactions()).containsExactly(deposit);
    }
    
    @Test
    void shouldThrowExceptionWhenDepositTransactionIsNull() {
        // Given
        Account account = new Account(accountId, customerId, accountType, initialBalance);
        
        // When & Then
        assertThatThrownBy(() -> account.deposit(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Transaction cannot be null");
    }
    
    @Test
    void shouldWithdrawMoneySuccessfully() throws InsufficientFundsException {
        // Given
        Account account = new Account(accountId, customerId, accountType, initialBalance);
        Transaction withdrawal = Transaction.create(BigDecimal.valueOf(300), "ATM withdrawal", WITHDRAWAL);
        Balance expectedNewBalance = initialBalance.subtract(Balance.of(BigDecimal.valueOf(300)));
        
        // When
        account.withdraw(withdrawal);
        
        // Then
        assertThat(account.getBalance()).isEqualTo(expectedNewBalance);
        assertThat(account.getTransactions()).hasSize(1);
        assertThat(account.getTransactions()).containsExactly(withdrawal);
    }
    
    @Test
    void shouldThrowExceptionWhenWithdrawalTransactionIsNull() {
        // Given
        Account account = new Account(accountId, customerId, accountType, initialBalance);
        
        // When & Then
        assertThatThrownBy(() -> account.withdraw(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Transaction cannot be null");
    }
    
    @Test
    void shouldThrowInsufficientFundsException() {
        // Given
        Account account = new Account(accountId, customerId, accountType, initialBalance);
        Transaction largeWithdrawal = Transaction.create(BigDecimal.valueOf(2000), "Large withdrawal", WITHDRAWAL);
        
        // When & Then
        assertThatThrownBy(() -> account.withdraw(largeWithdrawal))
            .isInstanceOf(InsufficientFundsException.class)
            .hasMessage("Insufficient funds for withdrawal");
        
        // Verify state hasn't changed
        assertThat(account.getBalance()).isEqualTo(initialBalance);
        assertThat(account.getTransactions()).isEmpty();
    }
    
    @Test
    void shouldTransferMoneySuccessfully() throws InsufficientFundsException {
        // Given
        Account sourceAccount = new Account(accountId, customerId, accountType, initialBalance);
        AccountId targetId = AccountId.generate();
        Account targetAccount = new Account(targetId, "customer-789", SAVINGS, Balance.of(BigDecimal.valueOf(500)));
        Transaction transfer = Transaction.create(BigDecimal.valueOf(400), "Transfer to savings", TRANSFER);
        
        Balance expectedSourceBalance = initialBalance.subtract(Balance.of(BigDecimal.valueOf(400)));
        Balance expectedTargetBalance = Balance.of(BigDecimal.valueOf(500)).add(Balance.of(BigDecimal.valueOf(400)));
        
        // When
        sourceAccount.transfer(targetAccount, transfer);
        
        // Then
        assertThat(sourceAccount.getBalance()).isEqualTo(expectedSourceBalance);
        assertThat(targetAccount.getBalance()).isEqualTo(expectedTargetBalance);
        assertThat(sourceAccount.getTransactions()).hasSize(1);
        assertThat(targetAccount.getTransactions()).hasSize(1);
    }
    
    @Test
    void shouldThrowExceptionWhenTransferTargetIsNull() throws InsufficientFundsException {
        // Given
        Account account = new Account(accountId, customerId, accountType, initialBalance);
        Transaction transfer = Transaction.create(BigDecimal.valueOf(100), "Transfer", TRANSFER);
        
        // When & Then
        assertThatThrownBy(() -> account.transfer(null, transfer))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Target account cannot be null");
    }
    
    @Test
    void shouldThrowExceptionWhenTransferTransactionIsNull() throws InsufficientFundsException {
        // Given
        Account sourceAccount = new Account(accountId, customerId, accountType, initialBalance);
        AccountId targetId = AccountId.generate();
        Account targetAccount = new Account(targetId, "customer-789", SAVINGS, Balance.of(BigDecimal.valueOf(500)));
        
        // When & Then
        assertThatThrownBy(() -> sourceAccount.transfer(targetAccount, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Transaction cannot be null");
    }
    
    @Test
    void shouldCheckSufficientBalance() {
        // Given
        Account account = new Account(accountId, customerId, accountType, initialBalance);
        
        // Then
        assertThat(account.hasSufficientBalance(BigDecimal.valueOf(500))).isTrue();
        assertThat(account.hasSufficientBalance(BigDecimal.valueOf(1000))).isTrue();
        assertThat(account.hasSufficientBalance(BigDecimal.valueOf(1500))).isFalse();
    }
    
    @Test
    void shouldReturnEmptyTransactionsWhenNoTransactions() {
        // Given
        Account account = new Account(accountId, customerId, accountType, initialBalance);
        
        // Then
        assertThat(account.getTransactions()).isEmpty();
    }
    
    @Test
    void shouldReturnTransactionsList() throws InsufficientFundsException {
        // Given
        Account account = new Account(accountId, customerId, accountType, initialBalance);
        Transaction deposit1 = Transaction.create(BigDecimal.valueOf(100), "Deposit 1", DEPOSIT);
        Transaction deposit2 = Transaction.create(BigDecimal.valueOf(200), "Deposit 2", DEPOSIT);
        Transaction withdrawal = Transaction.create(BigDecimal.valueOf(50), "Withdrawal", WITHDRAWAL);
        
        // When
        account.deposit(deposit1);
        account.deposit(deposit2);
        account.withdraw(withdrawal);
        
        // Then
        assertThat(account.getTransactions()).hasSize(3);
        assertThat(account.getTransactions()).containsExactly(deposit1, deposit2, withdrawal);
    }
    
    @Test
    void shouldClearDomainEvents() {
        // Given
        Account account = new Account(accountId, customerId, accountType, initialBalance);
        Transaction deposit = Transaction.create(BigDecimal.valueOf(100), "Deposit", DEPOSIT);
        
        // When
        account.deposit(deposit);
        assertThat(account.getDomainEvents()).hasSize(1);
        
        account.clearDomainEvents();
        
        // Then
        assertThat(account.getDomainEvents()).isEmpty();
    }
    
    @Test
    void shouldImplementEqualsAndHashCode() {
        // Given
        AccountId id1 = AccountId.of("account-1");
        AccountId id2 = AccountId.of("account-1");
        AccountId id3 = AccountId.of("account-2");
        
        Account account1 = new Account(id1, "customer-1", CHECKING, Balance.of(BigDecimal.valueOf(100)));
        Account account2 = new Account(id2, "customer-1", CHECKING, Balance.of(BigDecimal.valueOf(100)));
        Account account3 = new Account(id3, "customer-1", CHECKING, Balance.of(BigDecimal.valueOf(100)));
        
        // Then
        assertThat(account1).isEqualTo(account2);
        assertThat(account1.hashCode()).isEqualTo(account2.hashCode());
        assertThat(account1).isNotEqualTo(account3);
        assertThat(account1.hashCode()).isNotEqualTo(account3.hashCode());
    }
    
    @Test
    void shouldHandleMultipleDepositsAndWithdrawals() throws InsufficientFundsException {
        // Given
        Account account = new Account(accountId, customerId, accountType, Balance.of(BigDecimal.valueOf(1000)));
        
        // When - Multiple operations
        account.deposit(Transaction.create(BigDecimal.valueOf(500), "Deposit 1", DEPOSIT));
        account.deposit(Transaction.create(BigDecimal.valueOf(300), "Deposit 2", DEPOSIT));
        account.withdraw(Transaction.create(BigDecimal.valueOf(200), "Withdrawal 1", WITHDRAWAL));
        account.deposit(Transaction.create(BigDecimal.valueOf(100), "Deposit 3", DEPOSIT));
        account.withdraw(Transaction.create(BigDecimal.valueOf(150), "Withdrawal 2", WITHDRAWAL));
        
        // Then
        Balance expectedBalance = Balance.of(BigDecimal.valueOf(1000))
            .add(Balance.of(BigDecimal.valueOf(500)))
            .add(Balance.of(BigDecimal.valueOf(300)))
            .subtract(Balance.of(BigDecimal.valueOf(200)))
            .add(Balance.of(BigDecimal.valueOf(100)))
            .subtract(Balance.of(BigDecimal.valueOf(150)));
            
        assertThat(account.getBalance()).isEqualTo(expectedBalance);
        assertThat(account.getTransactions()).hasSize(5);
    }
}