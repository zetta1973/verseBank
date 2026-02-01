package com.versebank.accounts.infrastructure.web;

import com.versebank.accounts.application.port.in.TransferMoneyUseCase;
import com.versebank.accounts.domain.Account;
import com.versebank.accounts.domain.AccountId;
import com.versebank.accounts.domain.valueobjects.AccountType;
import com.versebank.accounts.domain.valueobjects.Balance;
import com.versebank.accounts.domain.exceptions.InsufficientFundsException;
import com.versebank.accounts.infrastructure.web.dto.AccountResponse;
import com.versebank.accounts.infrastructure.web.dto.TransferRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static com.versebank.accounts.domain.valueobjects.AccountType.*;

/**
 * Test suite for AccountController (Infrastructure Layer - Web)
 * 🧱 10-15% of total tests - REST controller and web layer testing
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccountController Tests")
class AccountControllerTest {
    
    @Mock
    private TransferMoneyUseCase transferMoneyUseCase;
    
    private AccountController accountController;
    private Account testAccount;
    
    @BeforeEach
    void setUp() {
        accountController = new AccountController(transferMoneyUseCase);
        testAccount = Account.create("customer-123", CHECKING, Balance.of(BigDecimal.valueOf(1000)));
    }
    
    @Test
    @DisplayName("Should transfer money successfully")
    void shouldTransferMoneySuccessfully() {
        // Given
        TransferRequest request = new TransferRequest(
            "source-account-123",
            "target-account-456", 
            BigDecimal.valueOf(200),
            "Payment for services"
        );
        
        // When
        ResponseEntity<String> response = accountController.transferMoney(request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Transfer completed successfully");
    }
    
    @Test
    @DisplayName("Should return bad request when insufficient funds")
    void shouldReturnBadRequestWhenInsufficientFunds() throws InsufficientFundsException {
        // Given
        TransferRequest request = new TransferRequest(
            "source-account-123",
            "target-account-456",
            BigDecimal.valueOf(2000), // Large amount
            "Large transfer"
        );
        
        doThrow(new InsufficientFundsException("Insufficient funds in account"))
            .when(transferMoneyUseCase).transferMoney(anyString(), anyString(), any(BigDecimal.class), anyString());
        
        // When
        ResponseEntity<String> response = accountController.transferMoney(request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Insufficient funds");
    }
    
    @Test
    @DisplayName("Should return bad request when invalid request")
    void shouldReturnBadRequestWhenInvalidRequest() throws InsufficientFundsException {
        // Given
        TransferRequest request = new TransferRequest(
            "source-account-123",
            "target-account-456",
            BigDecimal.valueOf(200),
            "Transfer"
        );
        
        doThrow(new IllegalArgumentException("Invalid account ID"))
            .when(transferMoneyUseCase).transferMoney(anyString(), anyString(), any(BigDecimal.class), anyString());
        
        // When
        ResponseEntity<String> response = accountController.transferMoney(request);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid request");
    }
    
    @Test
    void shouldDepositMoneySuccessfully() {
        // Given
        String accountId = "account-123";
        BigDecimal amount = BigDecimal.valueOf(500);
        String description = "Cash deposit";
        
        // When
        ResponseEntity<String> response = accountController.depositMoney(accountId, amount, description);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Deposit completed successfully");
    }
    
    @Test
    void shouldReturnBadRequestWhenDepositFails() {
        // Given
        String accountId = "non-existent-account";
        BigDecimal amount = BigDecimal.valueOf(100);
        String description = "Deposit";
        
        doThrow(new IllegalArgumentException("Account not found"))
            .when(transferMoneyUseCase).depositMoney(anyString(), any(BigDecimal.class), anyString());
        
        // When
        ResponseEntity<String> response = accountController.depositMoney(accountId, amount, description);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid request");
    }
    
    @Test
    void shouldWithdrawMoneySuccessfully() throws InsufficientFundsException {
        // Given
        String accountId = "account-123";
        BigDecimal amount = BigDecimal.valueOf(200);
        String description = "ATM withdrawal";
        
        // When
        ResponseEntity<String> response = accountController.withdrawMoney(accountId, amount, description);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Withdrawal completed successfully");
    }
    
    @Test
    void shouldReturnBadRequestWhenWithdrawalInsufficientFunds() throws InsufficientFundsException {
        // Given
        String accountId = "account-123";
        BigDecimal amount = BigDecimal.valueOf(2000);
        String description = "Large withdrawal";
        
        doThrow(new InsufficientFundsException("Insufficient funds"))
            .when(transferMoneyUseCase).withdrawMoney(anyString(), any(BigDecimal.class), anyString());
        
        // When
        ResponseEntity<String> response = accountController.withdrawMoney(accountId, amount, description);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Insufficient funds");
    }
    
    @Test
    void shouldReturnBadRequestWhenWithdrawalFails() throws InsufficientFundsException {
        // Given
        String accountId = "non-existent-account";
        BigDecimal amount = BigDecimal.valueOf(100);
        String description = "Withdrawal";
        
        doThrow(new IllegalArgumentException("Account not found"))
            .when(transferMoneyUseCase).withdrawMoney(anyString(), any(BigDecimal.class), anyString());
        
        // When
        ResponseEntity<String> response = accountController.withdrawMoney(accountId, amount, description);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid request");
    }
    
    @Test
    void shouldReturnAccountWhenFound() {
        // Given
        String accountId = "account-123";
        Account account = new Account(AccountId.of(accountId), "customer-456", SAVINGS, Balance.of(BigDecimal.valueOf(750)));
        
        when(transferMoneyUseCase.getAccount(accountId)).thenReturn(Optional.of(account));
        
        // When
        ResponseEntity<AccountResponse> response = accountController.getAccount(accountId);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        AccountResponse accountResponse = response.getBody();
        assertThat(accountResponse.getId()).isEqualTo(accountId);
        assertThat(accountResponse.getCustomerId()).isEqualTo("customer-456");
        assertThat(accountResponse.getAccountType()).isEqualTo(SAVINGS);
        assertThat(accountResponse.getBalance()).isEqualTo(BigDecimal.valueOf(750));
    }
    
    @Test
    void shouldReturnNotFoundWhenAccountDoesNotExist() {
        // Given
        String accountId = "non-existent-account";
        
        when(transferMoneyUseCase.getAccount(accountId)).thenReturn(Optional.empty());
        
        // When
        ResponseEntity<AccountResponse> response = accountController.getAccount(accountId);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
    
    @Test
    void shouldReturnBalanceWhenAccountFound() {
        // Given
        String accountId = "account-123";
        Account account = Account.create("customer-456", CHECKING, Balance.of(BigDecimal.valueOf(1250.75)));
        
        when(transferMoneyUseCase.getAccount(accountId)).thenReturn(Optional.of(account));
        
        // When
        ResponseEntity<BigDecimal> response = accountController.getBalance(accountId);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(BigDecimal.valueOf(1250.75));
    }
    
    @Test
    void shouldReturnNotFoundWhenBalanceQueryAccountDoesNotExist() {
        // Given
        String accountId = "non-existent-account";
        
        when(transferMoneyUseCase.getAccount(accountId)).thenReturn(Optional.empty());
        
        // When
        ResponseEntity<BigDecimal> response = accountController.getBalance(accountId);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }
    
    @Test
    void shouldCheckSufficientBalance() {
        // Given
        String accountId = "account-123";
        BigDecimal amount = BigDecimal.valueOf(500);
        
        when(transferMoneyUseCase.hasSufficientBalance(accountId, amount)).thenReturn(true);
        
        // When
        ResponseEntity<Boolean> response = accountController.hasSufficientBalance(accountId, amount);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
    }
    
    @Test
    void shouldReturnFalseForInsufficientBalance() {
        // Given
        String accountId = "account-123";
        BigDecimal amount = BigDecimal.valueOf(2000);
        
        when(transferMoneyUseCase.hasSufficientBalance(accountId, amount)).thenReturn(false);
        
        // When
        ResponseEntity<Boolean> response = accountController.hasSufficientBalance(accountId, amount);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isFalse();
    }
}
