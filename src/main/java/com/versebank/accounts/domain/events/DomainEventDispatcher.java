package com.versebank.accounts.domain.events;

import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DomainEventDispatcher {
    
    private static final Logger logger = LoggerFactory.getLogger(DomainEventDispatcher.class);
    
    @EventListener
    public void handleMoneyDepositedEvent(MoneyDepositedEvent event) {
        logger.info("💰 Money deposited: {} to account {}, new balance: {}", 
                   event.getAmount(), event.getAccountId(), event.getNewBalance());
        
        // Aquí podríamos enviar notificaciones, actualizar métricas, etc.
        // Por ejemplo: enviar email de confirmación, actualizar dashboards, etc.
    }
    
    @EventListener
    public void handleMoneyWithdrawnEvent(MoneyWithdrawnEvent event) {
        logger.info("💸 Money withdrawn: {} from account {}, new balance: {}", 
                   event.getAmount(), event.getAccountId(), event.getNewBalance());
        
        // Lógica específica para retiros
        // Podríamos verificar límites diarios, alertar por transacciones sospechosas, etc.
    }
    
    @EventListener
    public void handleAccountOpenedEvent(AccountOpenedEvent event) {
        logger.info("🏦 Account opened: {} for customer {} with initial balance: {}", 
                   event.getAccountId(), event.getCustomerId(), event.getInitialBalance());
        
        // Lógica para nuevas cuentas
        // Enviar bienvenida, configurar productos, etc.
    }
    
    @EventListener
    public void handleLargeTransactionDetectedEvent(LargeTransactionDetectedEvent event) {
        logger.info("⚠️  Large transaction detected: {} on account {} (type: {})", 
                   event.getAmount(), event.getAccountId(), event.getTransactionType());
        
        // Lógica de compliance y seguridad
        // Alertar al equipo de cumplimiento, guardar auditoría especial, etc.
    }
}