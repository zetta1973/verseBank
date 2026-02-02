package com.versebank;

import com.versebank.accounts.application.port.out.AccountRepository;
import com.versebank.accounts.application.port.out.NotificationPort;
import com.versebank.accounts.domain.Account;
import com.versebank.accounts.domain.AccountId;
import com.versebank.accounts.domain.valueobjects.AccountType;
import com.versebank.accounts.domain.valueobjects.Balance;
import com.versebank.accounts.infrastructure.notification.EmailNotificationAdapter;
import com.versebank.accounts.infrastructure.persistence.AccountJpaRepository;
import com.versebank.accounts.infrastructure.persistence.AccountRepositoryAdapter;

import com.versebank.users.domain.User;
import com.versebank.users.domain.UserId;
import com.versebank.users.domain.Email;
import com.versebank.users.domain.UserRepository;
import com.versebank.users.infrastructure.persistence.UserJpaRepository;
import com.versebank.users.infrastructure.persistence.UserRepositoryAdapter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.math.BigDecimal;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.versebank.accounts.infrastructure.persistence", "com.versebank.users.infrastructure.persistence"})
@ComponentScan(basePackages = {"com.versebank.accounts", "com.versebank.users"})
public class AccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }

    @Configuration
    static class BeanConfiguration {
        

        
        @Bean
        public AccountRepository accountRepository(AccountJpaRepository jpaRepository) {
            return new AccountRepositoryAdapter(jpaRepository);
        }
        
        @Bean
        public NotificationPort notificationPort() {
            return new EmailNotificationAdapter();
        }
        
        @Bean
        public UserRepository userRepository(UserJpaRepository jpaRepository) {
            return new UserRepositoryAdapter(jpaRepository);
        }
        
        @Bean
        public CommandLineRunner initData(
                AccountJpaRepository accountJpaRepository,
                UserJpaRepository userJpaRepository,
                AccountRepository accountRepository,
                UserRepository userRepository) {
            return args -> {
                // Initialize accounts if none exist
                if (accountJpaRepository.count() == 0) {
                    Account account1 = new Account(
                        AccountId.of("acc-001"),
                        "user-001",
                        AccountType.SAVINGS,
                        new Balance(BigDecimal.valueOf(1000.00))
                    );
                    
                    Account account2 = new Account(
                        AccountId.of("acc-002"),
                        "user-002",
                        AccountType.CHECKING,
                        new Balance(BigDecimal.valueOf(500.00))
                    );
                    
                    Account account3 = new Account(
                        AccountId.of("acc-003"),
                        "user-001",
                        AccountType.SAVINGS,
                        new Balance(BigDecimal.valueOf(2000.00))
                    );
                    
                    accountRepository.save(account1);
                    accountRepository.save(account2);
                    accountRepository.save(account3);
                    
                    System.out.println("✅ Sample accounts created successfully");
                }
                
                // Initialize users if none exist
                if (userJpaRepository.count() == 0) {
                    User user1 = new User(UserId.fromString("user-001"), "John Doe", Email.of("john.doe@example.com"));
                    User user2 = new User(UserId.fromString("user-002"), "Jane Smith", Email.of("jane.smith@example.com"));
                    
                    // Link accounts to users
                    user1.addAccount("acc-001");
                    user1.addAccount("acc-003");
                    user2.addAccount("acc-002");
                    
                    userRepository.save(user1);
                    userRepository.save(user2);
                    
                    System.out.println("✅ Sample users created successfully");
                }
                
                // Verification output
                System.out.println("\n=== System Initialization Complete ===");
                System.out.println("Total accounts: " + accountJpaRepository.count());
                System.out.println("Total users: " + userJpaRepository.count());
                System.out.println("=====================================\n");
            };
        }
    }
}
