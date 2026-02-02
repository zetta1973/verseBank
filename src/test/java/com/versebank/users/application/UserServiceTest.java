package com.versebank.users.application;

import com.versebank.accounts.application.port.in.AccountQueryPort;
import com.versebank.accounts.application.port.in.AccountSummary;
import com.versebank.users.application.port.in.CreateUserUseCase;
import com.versebank.users.application.port.in.GetUserByIdUseCase;
import com.versebank.users.application.port.in.LinkAccountToUserUseCase;
import com.versebank.users.domain.Email;
import com.versebank.users.domain.User;
import com.versebank.users.domain.UserId;
import com.versebank.users.domain.UserRepository;
import com.versebank.users.domain.exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private AccountQueryPort accountQueryPort;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        accountQueryPort = Mockito.mock(AccountQueryPort.class);
        userService = new UserService(userRepository, accountQueryPort);
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserId userId = userService.createUser("John Doe", "john@example.com");

        assertNotNull(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser("John Doe", "john@example.com");
        });
    }

    @Test
    void shouldGetUserById() {
        User user = User.create("John Doe", "john@example.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Optional<User> found = userService.getUser(user.getId());

        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
    }

    @Test
    void shouldLinkAccountToUser() {
        User user = User.create("John Doe", "john@example.com");
        AccountSummary accountSummary = new AccountSummary("acc-001", user.getId().getValue(), "SAVINGS", BigDecimal.valueOf(1000));
        
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountQueryPort.findByAccountId("acc-001")).thenReturn(Optional.of(accountSummary));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.linkAccountToUser(user.getId(), "acc-001");

        assertTrue(user.getAccountIds().contains("acc-001"));
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowExceptionWhenLinkingNonExistentAccount() {
        User user = User.create("John Doe", "john@example.com");
        
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(accountQueryPort.findByAccountId("non-existent")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            userService.linkAccountToUser(user.getId(), "non-existent");
        });
    }
}
