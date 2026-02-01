package com.versebank.accounts.infrastructure.notification;

import com.versebank.accounts.application.port.out.NotificationPort;
import com.versebank.accounts.domain.events.DomainEvent;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Primary
@Profile("!sms")
public class EmailNotificationAdapter implements NotificationPort {
    
    @Override
    public void sendNotification(String recipient, String subject, String message) {
        System.out.println("EMAIL NOTIFICATION");
        System.out.println("To: " + recipient);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("------------------------");
    }
    
    @Override
    public void sendEmail(String email, String subject, String message) {
        System.out.println("EMAIL SENT");
        System.out.println("Email: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("------------------------");
    }
    
    @Override
    public void sendSms(String phoneNumber, String message) {
        System.out.println("SMS NOTIFICATION");
        System.out.println("Phone: " + phoneNumber);
        System.out.println("Message: " + message);
        System.out.println("------------------------");
    }
    
    @Override
    public void notifyAccountOperation(String accountId, String operation, String details) {
        String subject = "Account Operation Notification";
        String message = "Account: " + accountId + " | Operation: " + operation + " | Details: " + details;
        sendNotification(accountId, subject, message);
    }
    
    @Override
    public void broadcastEvent(DomainEvent event) {
        System.out.println("DOMAIN EVENT BROADCASTED");
        System.out.println("Event: " + event.getEventType());
        System.out.println("ID: " + event.getEventId());
        System.out.println("Timestamp: " + event.getOccurredAt());
        System.out.println("------------------------");
    }
}