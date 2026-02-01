package com.versebank.accounts.infrastructure.notification;

import com.versebank.accounts.domain.events.DomainEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SmsNotificationAdapter Tests")
class SmsNotificationAdapterTest {

    private SmsNotificationAdapter smsNotificationAdapter;

    @BeforeEach
    void setUp() {
        smsNotificationAdapter = new SmsNotificationAdapter();
    }

    @Test
    @DisplayName("Should send notification successfully")
    void shouldSendNotificationSuccessfully() {
        // Given
        String recipient = "+1234567890";
        String subject = "Test Subject";
        String message = "Test message";

        // When & Then - Should not throw exception
        assertThatCode(() -> 
            smsNotificationAdapter.sendNotification(recipient, subject, message)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should send SMS successfully")
    void shouldSendSmsSuccessfully() {
        // Given
        String phoneNumber = "+1234567890";
        String message = "Test SMS message";

        // When & Then - Should not throw exception
        assertThatCode(() -> 
            smsNotificationAdapter.sendSms(phoneNumber, message)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should notify account operation")
    void shouldNotifyAccountOperation() {
        // Given
        String accountId = "account-123";
        String operation = "TRANSFER";
        String details = "Transfer of $100 to account-456";

        // When & Then - Should not throw exception
        assertThatCode(() -> 
            smsNotificationAdapter.notifyAccountOperation(accountId, operation, details)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should broadcast event successfully")
    void shouldBroadcastEventSuccessfully() {
        // Given
        DomainEvent testEvent = new TestDomainEvent("TEST_EVENT");

        // When & Then - Should not throw exception
        assertThatCode(() -> 
            smsNotificationAdapter.broadcastEvent(testEvent)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw exception when sending email")
    void shouldThrowExceptionWhenSendingEmail() {
        // Given
        String email = "test@example.com";
        String subject = "Test Subject";
        String message = "Test message";

        // When & Then
        assertThatThrownBy(() -> 
            smsNotificationAdapter.sendEmail(email, subject, message)
        ).isInstanceOf(UnsupportedOperationException.class)
         .hasMessage("SMS adapter cannot send emails");
    }

    @Test
    @DisplayName("Should handle null recipient gracefully")
    void shouldHandleNullRecipient() {
        // Given
        String subject = "Test";
        String message = "Test message";

        // When & Then - Should handle null gracefully
        assertThatCode(() -> 
            smsNotificationAdapter.sendNotification(null, subject, message)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle null phone number in SMS")
    void shouldHandleNullPhoneNumberInSms() {
        // Given
        String phoneNumber = null;
        String message = "Test SMS message";

        // When & Then - Should handle null gracefully
        assertThatCode(() -> 
            smsNotificationAdapter.sendSms(phoneNumber, message)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle empty message in SMS")
    void shouldHandleEmptyMessageInSms() {
        // Given
        String phoneNumber = "+1234567890";
        String message = "";

        // When & Then - Should handle empty message gracefully
        assertThatCode(() -> 
            smsNotificationAdapter.sendSms(phoneNumber, message)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle null account ID in account operation notification")
    void shouldHandleNullAccountIdInAccountOperationNotification() {
        // Given
        String accountId = null;
        String operation = "DEPOSIT";
        String details = "Amount: $50";

        // When & Then - Should handle null gracefully
        assertThatCode(() -> 
            smsNotificationAdapter.notifyAccountOperation(accountId, operation, details)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle null domain event")
    void shouldHandleNullDomainEvent() {
        // Given
        DomainEvent nullEvent = null;

        // When & Then - Should handle null gracefully
        assertThatThrownBy(() -> 
            smsNotificationAdapter.broadcastEvent(nullEvent)
        ).isInstanceOf(NullPointerException.class);
    }

    // Test helper class for DomainEvent testing
    private static class TestDomainEvent extends com.versebank.accounts.domain.events.DomainEvent {
        public TestDomainEvent(String eventType) {
            super(eventType);
        }
    }
}