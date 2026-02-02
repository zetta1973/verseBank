package com.versebank.users.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUser() {
        User user = User.create("John Doe", "john@example.com");
        
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail().getValue());
        assertTrue(user.getAccountIds().isEmpty());
    }

    @Test
    void shouldAddAccountToUser() {
        User user = User.create("John Doe", "john@example.com");
        user.addAccount("acc-001");
        
        assertEquals(1, user.getAccountIds().size());
        assertTrue(user.getAccountIds().contains("acc-001"));
    }

    @Test
    void shouldNotAddDuplicateAccount() {
        User user = User.create("John Doe", "john@example.com");
        user.addAccount("acc-001");
        user.addAccount("acc-001");
        
        assertEquals(1, user.getAccountIds().size());
    }

    @Test
    void shouldRemoveAccountFromUser() {
        User user = User.create("John Doe", "john@example.com");
        user.addAccount("acc-001");
        user.addAccount("acc-002");
        
        boolean removed = user.removeAccount("acc-001");
        
        assertTrue(removed);
        assertEquals(1, user.getAccountIds().size());
        assertFalse(user.getAccountIds().contains("acc-001"));
    }

    @Test
    void shouldValidateEmail() {
        assertThrows(IllegalArgumentException.class, () -> {
            Email.of("invalid-email");
        });
    }
}
