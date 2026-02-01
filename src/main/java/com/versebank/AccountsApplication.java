package com.versebank;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class AccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }

    @Configuration
    static class BeanConfiguration {
        
        @Bean
        public com.versebank.accounts.application.TransferMoneyService transferMoneyService(
                com.versebank.accounts.application.port.out.AccountRepository accountRepository,
                com.versebank.accounts.application.port.out.NotificationPort notificationPort) {
            return new com.versebank.accounts.application.TransferMoneyService(accountRepository, notificationPort);
        }
        
        @Bean
        public com.versebank.accounts.application.port.out.AccountRepository accountRepository(
                com.versebank.accounts.infrastructure.persistence.AccountJpaRepository jpaRepository) {
            return new com.versebank.accounts.infrastructure.persistence.AccountRepositoryAdapter(jpaRepository);
        }
        
        @Bean
        public com.versebank.accounts.application.port.out.NotificationPort notificationPort() {
            return new com.versebank.accounts.infrastructure.notification.EmailNotificationAdapter();
        }
        
        @Bean
        public CommandLineRunner initData(com.versebank.accounts.application.port.out.AccountRepository accountRepository, com.versebank.accounts.infrastructure.persistence.AccountJpaRepository jpaRepository) {
            return args -> {
                // Primero verificar si ya existen cuentas
                if (jpaRepository.count() == 0) {
                    // Crear cuentas de prueba
                    com.versebank.accounts.domain.Account account1 = new com.versebank.accounts.domain.Account(
                        com.versebank.accounts.domain.AccountId.of("acc-001"),
                        "customer-001",
                        com.versebank.accounts.domain.valueobjects.AccountType.SAVINGS,
                        new com.versebank.accounts.domain.valueobjects.Balance(java.math.BigDecimal.valueOf(1000.00))
                    );
                    
                    com.versebank.accounts.domain.Account account2 = new com.versebank.accounts.domain.Account(
                        com.versebank.accounts.domain.AccountId.of("acc-002"),
                        "customer-002",
                        com.versebank.accounts.domain.valueobjects.AccountType.CHECKING,
                        new com.versebank.accounts.domain.valueobjects.Balance(java.math.BigDecimal.valueOf(500.00))
                    );
                    
                    com.versebank.accounts.domain.Account account3 = new com.versebank.accounts.domain.Account(
                        com.versebank.accounts.domain.AccountId.of("acc-003"),
                        "customer-003",
                        com.versebank.accounts.domain.valueobjects.AccountType.SAVINGS,
                        new com.versebank.accounts.domain.valueobjects.Balance(java.math.BigDecimal.valueOf(2000.00))
                    );
                    
                    accountRepository.save(account1);
                    accountRepository.save(account2);
                    accountRepository.save(account3);
                    
                    System.out.println("Sample accounts created successfully");
                } else {
                    System.out.println("Database already has " + jpaRepository.count() + " accounts");
                }
                
                // Verificar que las cuentas se guardaron correctamente
                System.out.println("=== Verifying saved accounts ===");
                accountRepository.findById("acc-001").ifPresentOrElse(
                    acc -> System.out.println("Account acc-001 found with balance: " + acc.getBalance().getAmount()),
                    () -> System.out.println("Account acc-001 NOT found!")
                );
                accountRepository.findById("acc-002").ifPresentOrElse(
                    acc -> System.out.println("Account acc-002 found with balance: " + acc.getBalance().getAmount()),
                    () -> System.out.println("Account acc-002 NOT found!")
                );
                accountRepository.findById("acc-003").ifPresentOrElse(
                    acc -> System.out.println("Account acc-003 found with balance: " + acc.getBalance().getAmount()),
                    () -> System.out.println("Account acc-003 NOT found!")
                );
                System.out.println("=== End verification ===");
            };
        }
    }
}