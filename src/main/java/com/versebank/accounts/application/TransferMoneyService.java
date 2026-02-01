package com.versebank.accounts.application;

import com.versebank.accounts.application.port.in.TransferMoneyUseCase;
import com.versebank.accounts.application.port.out.AccountRepository;
import com.versebank.accounts.application.port.out.NotificationPort;
import com.versebank.accounts.domain.Account;
import com.versebank.accounts.domain.AccountDomainService;
import com.versebank.accounts.domain.valueobjects.Transaction;
import com.versebank.accounts.domain.exceptions.InsufficientFundsException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación que implementa los casos de uso para transferencias de dinero
 */
public class TransferMoneyService implements TransferMoneyUseCase {
    
    private final AccountRepository accountRepository;
    private final NotificationPort notificationPort;
    private final AccountDomainService accountDomainService;
    
    public TransferMoneyService(AccountRepository accountRepository, NotificationPort notificationPort) {
        if (accountRepository == null) {
            throw new NullPointerException("AccountRepository cannot be null");
        }
        if (notificationPort == null) {
            throw new NullPointerException("NotificationPort cannot be null");
        }
        this.accountRepository = accountRepository;
        this.notificationPort = notificationPort;
        this.accountDomainService = new AccountDomainService();
    }

    @Override
    public void transferMoney(String sourceAccountId, String targetAccountId, BigDecimal amount, String description) 
            throws InsufficientFundsException {
        
        Optional<Account> sourceOpt = accountRepository.findById(sourceAccountId);
        Optional<Account> targetOpt = accountRepository.findById(targetAccountId);
        
        if (sourceOpt.isEmpty()) {
            throw new IllegalArgumentException("Source account not found: " + sourceAccountId);
        }
        if (targetOpt.isEmpty()) {
            throw new IllegalArgumentException("Target account not found: " + targetAccountId);
        }
        
        Account sourceAccount = sourceOpt.get();
        Account targetAccount = targetOpt.get();
        
        // Calculate transfer fee using domain service
        BigDecimal transferFee = accountDomainService.calculateTransferFee(sourceAccount.getAccountType(), amount);
        
        Transaction sourceTransaction = Transaction.create(amount, description, Transaction.TransactionType.TRANSFER);
        
        // Apply transfer with fee to source account
        sourceAccount.withdraw(sourceTransaction);
        if (transferFee.compareTo(BigDecimal.ZERO) > 0) {
            Transaction feeTransaction = Transaction.create(transferFee, "Transfer fee", Transaction.TransactionType.FEE);
            sourceAccount.withdraw(feeTransaction);
        }
        
        // Deposit full amount to target account (receiver gets full amount)
        Transaction targetTransaction = Transaction.create(amount, description, Transaction.TransactionType.TRANSFER);
        targetAccount.deposit(targetTransaction);
        
        // Save both accounts
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
        
        // Send notifications
        String sourceDescription = "Transfer of " + amount + " to account " + targetAccountId;
        if (transferFee.compareTo(BigDecimal.ZERO) > 0) {
            sourceDescription += " (fee: " + transferFee + ")";
        }
        notificationPort.notifyAccountOperation(sourceAccountId, "TRANSFER_OUT", sourceDescription);
        notificationPort.notifyAccountOperation(targetAccountId, "TRANSFER_IN", 
            "Transfer of " + amount + " from account " + sourceAccountId);
    }

    @Override
    public void depositMoney(String accountId, BigDecimal amount, String description) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        
        Account account = accountOpt.get();
        Transaction transaction = Transaction.create(amount, description, Transaction.TransactionType.DEPOSIT);
        
        account.deposit(transaction);
        accountRepository.save(account);
        
        notificationPort.notifyAccountOperation(accountId, "DEPOSIT", 
            "Deposit of " + amount + " - " + description);
    }

    @Override
    public void withdrawMoney(String accountId, BigDecimal amount, String description) 
            throws InsufficientFundsException {
        
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        
        Account account = accountOpt.get();
        Transaction transaction = Transaction.create(amount, description, Transaction.TransactionType.WITHDRAWAL);
        
        account.withdraw(transaction);
        accountRepository.save(account);
        
        notificationPort.notifyAccountOperation(accountId, "WITHDRAWAL", 
            "Withdrawal of " + amount + " - " + description);
    }

    @Override
    public Optional<Account> getAccount(String accountId) {
        return accountRepository.findById(accountId);
    }

    @Override
    public List<Transaction> getAccountTransactions(String accountId) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        return accountOpt.map(Account::getTransactions).orElse(List.of());
    }

    @Override
    public boolean hasSufficientBalance(String accountId, BigDecimal amount) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        return accountOpt.map(account -> account.hasSufficientBalance(amount)).orElse(false);
    }
}
