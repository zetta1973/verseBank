package com.versebank.accounts.application.port.out;

import com.versebank.accounts.domain.events.DomainEvent;

/**
 * Puerto de salida para operaciones de notificación
 */
public interface NotificationPort {
    
    void sendNotification(String recipient, String subject, String message);
    
    void sendEmail(String email, String subject, String message);
    
    void sendSms(String phoneNumber, String message);
    
    void notifyAccountOperation(String accountId, String operation, String details);
    
    void broadcastEvent(DomainEvent event);
}