package com.versebank.accounts.infrastructure.notification;

import com.versebank.accounts.application.port.out.NotificationPort;
import com.versebank.accounts.domain.events.DomainEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("sms")
public class SmsNotificationAdapter implements NotificationPort {
    
    @Override
    public void sendNotification(String recipient, String subject, String message) {
        System.out.println("SMS NOTIFICATION");
        System.out.println("To: " + recipient);
        System.out.println("Message: " + message);
        System.out.println("------------------------");
    }
    
    @Override
    public void sendEmail(String email, String subject, String message) {
        throw new UnsupportedOperationException("SMS adapter cannot send emails");
    }
    
    @Override
    public void sendSms(String phoneNumber, String message) {
        System.out.println("SMS SENT");
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Message: " + message);
        System.out.println("------------------------");
    }
    
    @Override
    public void notifyAccountOperation(String accountId, String operation, String details) {
        String message = "Account: " + accountId + " | Operation: " + operation + " | Details: " + details;
        sendSms(accountId, message);
    }
    
    @Override
    public void broadcastEvent(DomainEvent event) {
        System.out.println("DOMAIN EVENT FOR SMS");
        System.out.println("Event: " + event.getEventType());
        System.out.println("ID: " + event.getEventId());
        System.out.println("Timestamp: " + event.getOccurredAt());
        System.out.println("------------------------");
    }
}