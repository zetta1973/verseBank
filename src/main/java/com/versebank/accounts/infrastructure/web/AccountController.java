package com.versebank.accounts.infrastructure.web;

import com.versebank.accounts.application.port.in.TransferMoneyUseCase;
import com.versebank.accounts.domain.exceptions.InsufficientFundsException;
import com.versebank.accounts.infrastructure.web.dto.TransferRequest;
import com.versebank.accounts.infrastructure.web.dto.AccountResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Controlador REST para operaciones de cuentas
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    private final TransferMoneyUseCase transferMoneyUseCase;

    public AccountController(TransferMoneyUseCase transferMoneyUseCase) {
        this.transferMoneyUseCase = transferMoneyUseCase;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody TransferRequest request) {
        try {
            transferMoneyUseCase.transferMoney(
                request.getSourceAccountId(),
                request.getTargetAccountId(),
                request.getAmount(),
                request.getDescription()
            );
            return ResponseEntity.ok("Transfer completed successfully");
        } catch (InsufficientFundsException e) {
            return ResponseEntity.badRequest().body("Insufficient funds: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<String> depositMoney(@PathVariable String accountId,
                                              @RequestParam BigDecimal amount,
                                              @RequestParam String description) {
        try {
            transferMoneyUseCase.depositMoney(accountId, amount, description);
            return ResponseEntity.ok("Deposit completed successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<String> withdrawMoney(@PathVariable String accountId,
                                              @RequestParam BigDecimal amount,
                                              @RequestParam String description) {
        try {
            transferMoneyUseCase.withdrawMoney(accountId, amount, description);
            return ResponseEntity.ok("Withdrawal completed successfully");
        } catch (InsufficientFundsException e) {
            return ResponseEntity.badRequest().body("Insufficient funds: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountId) {
        Optional<AccountResponse> accountOpt = transferMoneyUseCase.getAccount(accountId)
                .map(account -> new AccountResponse(
                    account.getId().getValue(),
                    account.getCustomerId(),
                    account.getAccountType(),
                    account.getBalance().getAmount(),
                    java.time.LocalDateTime.now(), // Placeholder
                    java.time.LocalDateTime.now() // Placeholder
                ));
        
        return accountOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountId) {
        Optional<BigDecimal> balanceOpt = transferMoneyUseCase.getAccount(accountId)
                .map(account -> account.getBalance().getAmount());
        
        return balanceOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{accountId}/has-sufficient-balance")
    public ResponseEntity<Boolean> hasSufficientBalance(@PathVariable String accountId,
                                                      @RequestParam BigDecimal amount) {
        boolean hasSufficient = transferMoneyUseCase.hasSufficientBalance(accountId, amount);
        return ResponseEntity.ok(hasSufficient);
    }
}