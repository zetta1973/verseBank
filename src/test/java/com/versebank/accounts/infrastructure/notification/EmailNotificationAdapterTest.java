package com.versebank.accounts.infrastructure.notification;

import com.versebank.accounts.application.port.out.NotificationPort;
import com.versebank.accounts.domain.events.DomainEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.*;

/**
 * Test suite for EmailNotificationAdapter (Infrastructure Layer - Notification)
 * 🧱 10-15% of total tests - Notification adapter testing
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmailNotificationAdapter Tests")
class EmailNotificationAdapterTest {
    
    private EmailNotificationAdapter emailNotificationAdapter;
    private ByteArrayOutputStream outContent;
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        emailNotificationAdapter = new EmailNotificationAdapter();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    @Test
    @DisplayName("Should send notification successfully")
    void shouldSendNotificationSuccessfully() {
        // Given
        String recipient = "user@example.com";
        String subject = "Test Notification";
        String message = "This is a test message";
        
        // When
        emailNotificationAdapter.sendNotification(recipient, subject, message);
        
        // Then
        String output = outContent.toString();
        assertThat(output)
            .as("Email notification output")
            .contains("EMAIL NOTIFICATION")
            .contains("To: " + recipient)
            .contains("Subject: " + subject)
            .contains("Message: " + message);
    }
    
    @Test
    @DisplayName("Should send email successfully")
    void shouldSendEmailSuccessfully() {
        // Given
        String email = "test@example.com";
        String subject = "Account Update";
        String message = "Your account has been updated";
        
        // When
        emailNotificationAdapter.sendEmail(email, subject, message);
        
        // Then
        String output = outContent.toString();
        assertThat(output)
            .as("Email sent output")
            .contains("EMAIL SENT")
            .contains("Email: " + email)
            .contains("Subject: " + subject)
            .contains("Message: " + message);
    }
    
    @Test
    @DisplayName("Should send SMS notification")
    void shouldSendSms() {
        // Given
        String phoneNumber = "+1234567890";
        String message = "SMS message content";
        
        // When
        emailNotificationAdapter.sendSms(phoneNumber, message);
        
        // Then
        String output = outContent.toString();
        assertThat(output)
            .as("SMS notification output")
            .contains("SMS NOTIFICATION")
            .contains("Phone: " + phoneNumber)
            .contains("Message: " + message);
    }
    
    @Test
    @DisplayName("Should notify account operation")
    void shouldNotifyAccountOperation() {
        // Given
        String accountId = "account-123";
        String operation = "TRANSFER";
        String details = "Transfer of $100 to account-456";
        
        // When
        emailNotificationAdapter.notifyAccountOperation(accountId, operation, details);
        
        // Then
        String output = outContent.toString();
        assertThat(output)
            .as("Account operation notification")
            .contains("EMAIL NOTIFICATION")
            .contains("To: " + accountId)
            .contains("Subject: Account Operation Notification")
            .contains("Account: " + accountId)
            .contains("Operation: " + operation)
            .contains("Details: " + details);
    }
    
    @Test
    void shouldBroadcastDomainEvent() {
        // Given
        DomainEvent event = new TestDomainEvent("MoneyDeposited");
        
        // When
        emailNotificationAdapter.broadcastEvent(event);
        
        // Then
        String output = outContent.toString();
        assertThat(output).contains("DOMAIN EVENT BROADCASTED");
        assertThat(output).contains("Event: MoneyDeposited");
        assertThat(output).contains("ID: " + event.getEventId());
        assertThat(output).contains("Timestamp: " + event.getOccurredAt());
    }
    
    @Test
    @DisplayName("Should handle null recipient gracefully")
    void shouldHandleNullRecipient() {
        // Given
        String subject = "Test";
        String message = "Test message";
        
        // When
        assertThatCode(() -> emailNotificationAdapter.sendNotification(null, subject, message))
            .as("Sending notification with null recipient")
            .doesNotThrowAnyException();
        
        // Then
        String output = outContent.toString();
        assertThat(output).contains("EMAIL NOTIFICATION");
    }
    
    @Test
    @DisplayName("Should handle empty recipient gracefully")
    void shouldHandleEmptyRecipient() {
        // Given
        String subject = "Test";
        String message = "Test message";
        
        // When
        assertThatCode(() -> emailNotificationAdapter.sendNotification("", subject, message))
            .as("Sending notification with empty recipient")
            .doesNotThrowAnyException();
        
        // Then
        String output = outContent.toString();
        assertThat(output).contains("EMAIL NOTIFICATION");
    }
    
    @Test
    void shouldHandleNullSubject() {
        // Given
        String recipient = "test@example.com";
        String message = "Test message";
        
        // When
        emailNotificationAdapter.sendNotification(recipient, null, message);
        
        // Then - Should not throw exception
        String output = outContent.toString();
        assertThat(output).contains("EMAIL NOTIFICATION");
    }
    
    @Test
    void shouldHandleNullMessage() {
        // Given
        String recipient = "test@example.com";
        String subject = "Test";
        
        // When
        emailNotificationAdapter.sendNotification(recipient, subject, null);
        
        // Then - Should not throw exception
        String output = outContent.toString();
        assertThat(output).contains("EMAIL NOTIFICATION");
    }
    
    @Test
    void shouldHandleLongMessages() {
        // Given
        String recipient = "user@example.com";
        String subject = "Very Long Subject Line";
        String longMessage = "This is a very long message ".repeat(100);
        
        // When
        emailNotificationAdapter.sendNotification(recipient, subject, longMessage);
        
        // Then
        String output = outContent.toString();
        assertThat(output).contains("EMAIL NOTIFICATION");
        assertThat(output).contains("To: " + recipient);
        assertThat(output).contains("Subject: " + subject);
        assertThat(output).contains("Message: " + longMessage);
    }
    
    @Test
    void shouldHandleSpecialCharactersInMessage() {
        // Given
        String recipient = "user@example.com";
        String subject = "Test with special chars: ñáéíóú@#$%^&*()";
        String message = "Message with émojis 🎉 and spëcial çhars!";
        
        // When
        emailNotificationAdapter.sendNotification(recipient, subject, message);
        
        // Then
        String output = outContent.toString();
        assertThat(output).contains("EMAIL NOTIFICATION");
        assertThat(output).contains("To: " + recipient);
        assertThat(output).contains("Subject: " + subject);
        assertThat(output).containsIgnoringCase("Message:").containsIgnoringCase("and");
    }
    
    @Test
    void shouldDifferentiateBetweenEmailAndSmsOutput() {
        // When - Send email
        emailNotificationAdapter.sendEmail("test@example.com", "Email Subject", "Email message");
        String emailOutput = outContent.toString();
        
        // Reset stream
        outContent.reset();
        
        // When - Send SMS
        emailNotificationAdapter.sendSms("+1234567890", "SMS message");
        String smsOutput = outContent.toString();
        
        // Then
        assertThat(emailOutput).contains("EMAIL SENT");
        assertThat(emailOutput).doesNotContain("SMS");
        
        assertThat(smsOutput).contains("SMS NOTIFICATION");
        assertThat(smsOutput).doesNotContain("EMAIL SENT");
    }
    
    @Test
    @DisplayName("Should implement NotificationPort interface")
    void shouldImplementNotificationPortInterface() {
        // Then
        assertThat(emailNotificationAdapter)
            .as("EmailNotificationAdapter implementation")
            .isInstanceOf(NotificationPort.class);
    }
    
    @Test
    @DisplayName("Should provide separate methods for email and SMS")
    void shouldProvideSeparateMethodsForEmailAndSms() {
        // When & Then - Both methods should be callable without exceptions
        assertThatCode(() -> {
            emailNotificationAdapter.sendEmail("test@example.com", "Subject", "Message");
            emailNotificationAdapter.sendSms("+1234567890", "Message");
        }).as("Email and SMS methods")
          .doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("Should generate correct output format")
    void shouldGenerateCorrectOutputFormat() {
        // Given
        String recipient = "recipient@test.com";
        String subject = "Test Subject";
        String message = "Test Message";
        
        // When
        emailNotificationAdapter.sendNotification(recipient, subject, message);
        String output = outContent.toString();
        
        // Then - Verify format includes expected sections
        String[] lines = output.split("\\n");
        
        assertThat(lines[0].trim())
            .as("First line - notification type")
            .isEqualTo("EMAIL NOTIFICATION");
        assertThat(lines[1])
            .as("Second line - recipient")
            .contains("To: " + recipient);
        assertThat(lines[2])
            .as("Third line - subject")
            .contains("Subject: " + subject);
        assertThat(lines[3])
            .as("Fourth line - message")
            .contains("Message: " + message);
        assertThat(lines[4].trim())
            .as("Fifth line - separator")
            .isEqualTo("------------------------");
    }
    
    @Test
    void shouldHandleMultipleNotifications() {
        // When - Send multiple notifications
        emailNotificationAdapter.sendNotification("user1@test.com", "First", "Message 1");
        emailNotificationAdapter.sendNotification("user2@test.com", "Second", "Message 2");
        emailNotificationAdapter.sendEmail("user3@test.com", "Third", "Message 3");
        
        String output = outContent.toString();
        
        // Then - All should be in output
        assertThat(output).contains("EMAIL NOTIFICATION");
        assertThat(output).contains("EMAIL SENT");
        assertThat(output).contains("user1@test.com");
        assertThat(output).contains("user2@test.com");
        assertThat(output).contains("user3@test.com");
        assertThat(output).contains("First");
        assertThat(output).contains("Second");
        assertThat(output).contains("Third");
    }
    
    @Test
    void shouldBroadcastDifferentDomainEventTypes() {
        // Test different event types
        
        // MoneyDeposited event
        DomainEvent depositEvent = new TestDomainEvent("MoneyDeposited");
        outContent.reset();
        emailNotificationAdapter.broadcastEvent(depositEvent);
        String depositOutput = outContent.toString();
        assertThat(depositOutput).contains("Event: MoneyDeposited");
        
        // MoneyWithdrawn event
        DomainEvent withdrawalEvent = new TestDomainEvent("MoneyWithdrawn");
        outContent.reset();
        emailNotificationAdapter.broadcastEvent(withdrawalEvent);
        String withdrawalOutput = outContent.toString();
        assertThat(withdrawalOutput).contains("Event: MoneyWithdrawn");
        
        // AccountCreated event
        DomainEvent creationEvent = new TestDomainEvent("AccountCreated");
        outContent.reset();
        emailNotificationAdapter.broadcastEvent(creationEvent);
        String creationOutput = outContent.toString();
        assertThat(creationOutput).contains("Event: AccountCreated");
    }
    
    // Helper class for testing domain events
    private static class TestDomainEvent extends DomainEvent {
        public TestDomainEvent(String eventType) {
            super(eventType);
        }
    }
}
