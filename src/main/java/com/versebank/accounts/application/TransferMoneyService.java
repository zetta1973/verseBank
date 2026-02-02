package com.versebank.accounts.application;

import com.versebank.accounts.application.port.out.AccountRepository;
import com.versebank.accounts.application.port.out.NotificationPort;
import com.versebank.accounts.domain.Account;

import com.versebank.accounts.domain.valueobjects.Transaction;
import com.versebank.accounts.domain.exceptions.InsufficientFundsException;
import com.versebank.accounts.domain.events.DomainEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación que implementa los casos de uso para transferencias de dinero
 */
@Service
public class TransferMoneyService implements com.versebank.accounts.application.port.in.TransferMoneyUseCase {
    
    private final AccountRepository accountRepository;
    private final NotificationPort notificationPort;

    private final ApplicationEventPublisher eventPublisher;
    
    public TransferMoneyService(AccountRepository accountRepository, NotificationPort notificationPort, ApplicationEventPublisher eventPublisher) {
        if (accountRepository == null) {
            throw new NullPointerException("AccountRepository cannot be null");
        }
        if (notificationPort == null) {
            throw new NullPointerException("NotificationPort cannot be null");
        }
        if (eventPublisher == null) {
            throw new NullPointerException("ApplicationEventPublisher cannot be null");
        }
        this.accountRepository = accountRepository;
        this.notificationPort = notificationPort;

        this.eventPublisher = eventPublisher;
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
        
        // Validar transferencia usando lógica de dominio
        if (!sourceAccount.canTransferTo(targetAccount, amount)) {
            throw new InsufficientFundsException("Transfer not allowed: insufficient funds or invalid target");
        }
        
        // Calculate transfer fee using domain logic
        BigDecimal transferFee = sourceAccount.calculateTransferFee();
        
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

        // Publicar eventos
        publishAndClearDomainEvents(sourceAccount);
        publishAndClearDomainEvents(targetAccount);

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

        // Publicar eventos
        publishAndClearDomainEvents(account);

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

        // Publicar eventos
        publishAndClearDomainEvents(account);

        accountRepository.save(account);

        notificationPort.notifyAccountOperation(accountId, "WITHDRAWAL",
            "Withdrawal of " + amount + " - " + description);
    }

    @Override
    public boolean hasSufficientBalance(String accountId, BigDecimal amount) {
        Optional<Account> accountOpt = accountRepository.findById(accountId);
        return accountOpt.map(account -> account.hasSufficientBalance(amount)).orElse(false);
    }

    /**
     * Método helper para publicar y limpiar eventos de dominio (DRY)
     */
    private void publishAndClearDomainEvents(Account account) {
        List<DomainEvent> domainEvents = new ArrayList<>(account.getDomainEvents());
        domainEvents.forEach(eventPublisher::publishEvent);
        account.clearDomainEvents();
    }
}