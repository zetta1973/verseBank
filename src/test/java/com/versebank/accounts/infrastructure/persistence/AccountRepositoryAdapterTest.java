package com.versebank.accounts.infrastructure.persistence;

import com.versebank.accounts.domain.Account;
import com.versebank.accounts.domain.AccountId;
import com.versebank.accounts.domain.valueobjects.AccountType;
import com.versebank.accounts.domain.valueobjects.Balance;
import com.versebank.accounts.domain.valueobjects.Transaction;
import com.versebank.accounts.domain.events.DomainEvent;
import com.versebank.accounts.domain.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static com.versebank.accounts.domain.valueobjects.AccountType.*;

/**
 * Test suite for AccountRepositoryAdapter (Infrastructure Layer - Persistence)
 * 🧱 10-15% of total tests - Repository adapter and persistence testing
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccountRepositoryAdapter Tests")
class AccountRepositoryAdapterTest {
    
    @Mock
    private AccountJpaRepository jpaRepository;
    
    private AccountRepositoryAdapter accountRepositoryAdapter;
    
    private Account testAccount;
    private AccountJpaEntity testJpaEntity;
    
    @BeforeEach
    void setUp() {
        accountRepositoryAdapter = new AccountRepositoryAdapter(jpaRepository);
        
        // Setup test account
        AccountId accountId = AccountId.of("test-account-123");
        testAccount = new Account(accountId, "customer-456", CHECKING, Balance.of(BigDecimal.valueOf(1000)));
        
        // Setup test JPA entity
        testJpaEntity = new AccountJpaEntity(
            "test-account-123",
            "customer-456",
            CHECKING,
            BigDecimal.valueOf(1000),
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );
    }
    
    @Test
    @DisplayName("Should find account by ID")
    void shouldFindAccountById() {
        // Given
        when(jpaRepository.findById("test-account-123")).thenReturn(Optional.of(testJpaEntity));
        
        // When
        Optional<Account> result = accountRepositoryAdapter.findById("test-account-123");
        
        // Then
        assertThat(result).isPresent();
        Account foundAccount = result.get();
        assertThat(foundAccount.getId().getValue()).isEqualTo("test-account-123");
        assertThat(foundAccount.getCustomerId()).isEqualTo("customer-456");
        assertThat(foundAccount.getAccountType()).isEqualTo(CHECKING);
        assertThat(foundAccount.getBalance()).isEqualTo(Balance.of(BigDecimal.valueOf(1000)));
        
        verify(jpaRepository).findById("test-account-123");
    }
    
    @Test
    @DisplayName("Should return empty when account not found")
    void shouldReturnEmptyWhenAccountNotFound() {
        // Given
        when(jpaRepository.findById("non-existent-account")).thenReturn(Optional.empty());
        
        // When
        Optional<Account> result = accountRepositoryAdapter.findById("non-existent-account");
        
        // Then
        assertThat(result).isNotPresent();
        verify(jpaRepository).findById("non-existent-account");
    }
    
    @Test
    @DisplayName("Should save new account")
    void shouldSaveNewAccount() {
        // Given
        when(jpaRepository.findById(anyString())).thenReturn(Optional.empty());
        when(jpaRepository.save(any(AccountJpaEntity.class))).thenReturn(testJpaEntity);
        
        Account newAccount = Account.create("new-customer", SAVINGS, Balance.of(BigDecimal.valueOf(500)));
        
        // When
        Account savedAccount = accountRepositoryAdapter.save(newAccount);
        
        // Then
        assertThat(savedAccount).isNotNull();
        verify(jpaRepository).findById(newAccount.getId().getValue());
        verify(jpaRepository).save(any(AccountJpaEntity.class));
    }
    
    @Test
    void shouldUpdateExistingAccount() {
        // Given
        Account existingAccount = Account.create("existing-customer", BUSINESS, Balance.of(BigDecimal.valueOf(2000)));
        
        when(jpaRepository.findById(existingAccount.getId().getValue())).thenReturn(Optional.of(testJpaEntity));
        when(jpaRepository.save(any(AccountJpaEntity.class))).thenReturn(testJpaEntity);
        
        // When
        Account savedAccount = accountRepositoryAdapter.save(existingAccount);
        
        // Then
        assertThat(savedAccount).isNotNull();
        verify(jpaRepository, times(1)).findById(existingAccount.getId().getValue());
        verify(jpaRepository).save(any(AccountJpaEntity.class));
    }
    
    @Test
    void shouldDeleteAccountById() {
        // Given
        String accountId = "account-to-delete";
        
        // When
        accountRepositoryAdapter.deleteById(accountId);
        
        // Then
        verify(jpaRepository).deleteById(accountId);
    }
    
    @Test
    void shouldCheckAccountExistence() {
        // Given
        String existingAccountId = "existing-account";
        String nonExistingAccountId = "non-existing-account";
        
        when(jpaRepository.existsById(existingAccountId)).thenReturn(true);
        when(jpaRepository.existsById(nonExistingAccountId)).thenReturn(false);
        
        // When
        boolean exists = accountRepositoryAdapter.existsById(existingAccountId);
        boolean notExists = accountRepositoryAdapter.existsById(nonExistingAccountId);
        
        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
        
        verify(jpaRepository).existsById(existingAccountId);
        verify(jpaRepository).existsById(nonExistingAccountId);
    }
    
    @Test
    void shouldFindAccountsByCustomerId() {
        // Given
        String customerId = "customer-123";
        
        AccountJpaEntity entity1 = new AccountJpaEntity(
            "account-1", customerId, CHECKING, BigDecimal.valueOf(1000),
            java.time.LocalDateTime.now(), java.time.LocalDateTime.now()
        );
        AccountJpaEntity entity2 = new AccountJpaEntity(
            "account-2", customerId, SAVINGS, BigDecimal.valueOf(500),
            java.time.LocalDateTime.now(), java.time.LocalDateTime.now()
        );
        
        when(jpaRepository.findByCustomerId(customerId)).thenReturn(List.of(entity1, entity2));
        
        // When
        List<Account> accounts = accountRepositoryAdapter.findByCustomerId(customerId);
        
        // Then
        assertThat(accounts).hasSize(2);
        assertThat(accounts.get(0).getId().getValue()).isEqualTo("account-1");
        assertThat(accounts.get(1).getId().getValue()).isEqualTo("account-2");
        assertThat(accounts.get(0).getCustomerId()).isEqualTo(customerId);
        assertThat(accounts.get(1).getCustomerId()).isEqualTo(customerId);
        
        verify(jpaRepository).findByCustomerId(customerId);
    }
    
    @Test
    void shouldReturnEmptyListWhenNoAccountsForCustomer() {
        // Given
        String customerId = "customer-with-no-accounts";
        
        when(jpaRepository.findByCustomerId(customerId)).thenReturn(List.of());
        
        // When
        List<Account> accounts = accountRepositoryAdapter.findByCustomerId(customerId);
        
        // Then
        assertThat(accounts).isEmpty();
        verify(jpaRepository).findByCustomerId(customerId);
    }
    
    @Test
    void shouldSaveDomainEvent() {
        // Given
        DomainEvent event = new TestDomainEvent("TestEvent");
        
        // When
        accountRepositoryAdapter.saveDomainEvent(event);
        
        // Then
        // Note: Current implementation just prints to console
        // In a real implementation, this would persist to an events table
        verifyNoInteractions(jpaRepository); // Should not interact with account repository
    }
    
    @Test
    void shouldReturnEmptyDomainEventsList() {
        // Given
        String accountId = "test-account-123";
        
        // When
        List<DomainEvent> events = accountRepositoryAdapter.getDomainEvents(accountId);
        
        // Then
        assertThat(events).isEmpty();
        verifyNoInteractions(jpaRepository); // Should not interact with account repository
    }
    
    @Test
    void shouldHandleAccountWithTransactions() throws InsufficientFundsException {
        // Given
        Account accountWithTransactions = Account.create("customer", CHECKING, Balance.of(BigDecimal.valueOf(1000)));
        
        // Add some transactions
        accountWithTransactions.deposit(Transaction.create(BigDecimal.valueOf(200), "Deposit", Transaction.TransactionType.DEPOSIT));
        accountWithTransactions.withdraw(Transaction.create(BigDecimal.valueOf(50), "Withdrawal", Transaction.TransactionType.WITHDRAWAL));
        
        when(jpaRepository.findById(accountWithTransactions.getId().getValue())).thenReturn(Optional.empty());
        when(jpaRepository.save(any(AccountJpaEntity.class))).thenReturn(testJpaEntity);
        
        // When
        Account savedAccount = accountRepositoryAdapter.save(accountWithTransactions);
        
        // Then
        assertThat(savedAccount).isNotNull();
        verify(jpaRepository).save(any(AccountJpaEntity.class));
    }
    
    @Test
    void shouldHandleDifferentAccountTypes() {
        // Test each account type
        
        // Savings Account
        Account savingsAccount = Account.create("customer-1", SAVINGS, Balance.of(BigDecimal.valueOf(500)));
        when(jpaRepository.findById(savingsAccount.getId().getValue())).thenReturn(Optional.empty());
        when(jpaRepository.save(any(AccountJpaEntity.class))).thenReturn(testJpaEntity);
        
        Account savedSavingsAccount = accountRepositoryAdapter.save(savingsAccount);
        assertThat(savedSavingsAccount).isNotNull();
        
        // Business Account
        Account businessAccount = Account.create("customer-2", BUSINESS, Balance.of(BigDecimal.valueOf(10000)));
        when(jpaRepository.findById(businessAccount.getId().getValue())).thenReturn(Optional.empty());
        when(jpaRepository.save(any(AccountJpaEntity.class))).thenReturn(testJpaEntity);
        
        Account savedBusinessAccount = accountRepositoryAdapter.save(businessAccount);
        assertThat(savedBusinessAccount).isNotNull();
        
        verify(jpaRepository, times(2)).save(any(AccountJpaEntity.class));
    }
    
    @Test
    void shouldHandleZeroBalance() {
        // Given
        Account zeroBalanceAccount = Account.create("customer", CHECKING, Balance.zero());
        
        AccountJpaEntity zeroBalanceJpaEntity = new AccountJpaEntity(
            zeroBalanceAccount.getId().getValue(),
            "customer",
            CHECKING,
            BigDecimal.ZERO,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );
        
        when(jpaRepository.findById(zeroBalanceAccount.getId().getValue())).thenReturn(Optional.empty());
        when(jpaRepository.save(any(AccountJpaEntity.class))).thenReturn(zeroBalanceJpaEntity);
        
        // When
        Account savedAccount = accountRepositoryAdapter.save(zeroBalanceAccount);
        
        // Then
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.getBalance()).isEqualTo(Balance.zero());
        verify(jpaRepository).save(any(AccountJpaEntity.class));
    }
    
    @Test
    void shouldHandleLargeBalance() {
        // Given
        BigDecimal largeBalance = new BigDecimal("999999999.99");
        Account largeBalanceAccount = Account.create("customer", CHECKING, Balance.of(largeBalance));
        
        AccountJpaEntity largeBalanceEntity = new AccountJpaEntity(
            largeBalanceAccount.getId().getValue(),
            "customer",
            CHECKING,
            largeBalance,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );
        
        when(jpaRepository.findById(largeBalanceAccount.getId().getValue())).thenReturn(Optional.empty());
        when(jpaRepository.save(any(AccountJpaEntity.class))).thenReturn(largeBalanceEntity);
        
        // When
        Account savedAccount = accountRepositoryAdapter.save(largeBalanceAccount);
        
        // Then
        assertThat(savedAccount).isNotNull();
        assertThat(savedAccount.getBalance().getAmount()).isEqualTo(largeBalance);
        verify(jpaRepository).save(any(AccountJpaEntity.class));
    }
    
    @Test
    void shouldRoundTripAccountData() {
        // Given
        String accountId = "round-trip-account";
        String customerId = "round-trip-customer";
        AccountType accountType = CHECKING;
        BigDecimal balance = BigDecimal.valueOf(1234.56);
        
        // Create account
        Account originalAccount = new Account(
            AccountId.of(accountId),
            customerId,
            accountType,
            Balance.of(balance)
        );
        
        // Create corresponding JPA entity
        AccountJpaEntity jpaEntity = new AccountJpaEntity(
            accountId,
            customerId,
            accountType,
            balance,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );
        
        // When - simulate save and find
        when(jpaRepository.findById(accountId)).thenReturn(Optional.empty());
        when(jpaRepository.save(any(AccountJpaEntity.class))).thenReturn(jpaEntity);
        
        Account savedAccount = accountRepositoryAdapter.save(originalAccount);
        
        // Update mock to return the saved entity
        when(jpaRepository.findById(accountId)).thenReturn(Optional.of(jpaEntity));
        
        Account retrievedAccount = accountRepositoryAdapter.findById(accountId).orElseThrow();
        
        // Then
        assertThat(retrievedAccount.getId()).isEqualTo(originalAccount.getId());
        assertThat(retrievedAccount.getCustomerId()).isEqualTo(originalAccount.getCustomerId());
        assertThat(retrievedAccount.getAccountType()).isEqualTo(originalAccount.getAccountType());
        assertThat(retrievedAccount.getBalance()).isEqualTo(originalAccount.getBalance());
    }
    
    // Helper class for testing domain events
    private static class TestDomainEvent extends DomainEvent {
        public TestDomainEvent(String eventType) {
            super(eventType);
        }
    }
}
