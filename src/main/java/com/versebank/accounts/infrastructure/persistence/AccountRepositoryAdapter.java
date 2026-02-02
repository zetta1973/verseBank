package com.versebank.accounts.infrastructure.persistence;

import com.versebank.accounts.application.port.in.AccountSummary;
import com.versebank.accounts.application.port.out.AccountRepository;
import com.versebank.accounts.domain.Account;
import com.versebank.accounts.domain.events.DomainEvent;
import com.versebank.accounts.infrastructure.persistence.mappers.AccountMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa el puerto AccountRepository usando JPA
 */
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJpaRepository jpaRepository;
    // Simulación en memoria para persistencia de eventos de dominio
    private final Map<String, List<DomainEvent>> domainEventsStore = new ConcurrentHashMap<>();

    public AccountRepositoryAdapter(AccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Account> findById(String accountId) {
        return jpaRepository.findById(accountId)
                .map(AccountMapper::toDomain);
    }

    @Override
    public Account save(Account account) {
        Optional<AccountJpaEntity> existingEntity = jpaRepository.findById(account.getId().getValue());

        // Guardar eventos de dominio antes de modificar la cuenta
        // Esto es una simulación. En un sistema real, esto podría ser parte de una Unit of Work
        // o un mecanismo de Outbox.
        List<DomainEvent> eventsToSave = new ArrayList<>(account.getDomainEvents());
        if (!eventsToSave.isEmpty()) {
            saveDomainEvents(account.getId().getValue(), eventsToSave);
            account.clearDomainEvents(); // Limpiar después de guardar
        }

        if (existingEntity.isPresent()) {
            // Update existing account
            AccountJpaEntity jpaEntity = existingEntity.get();
            AccountMapper.updateJpaFromDomain(account, jpaEntity);
            AccountJpaEntity savedEntity = jpaRepository.save(jpaEntity);
            return AccountMapper.toDomain(savedEntity);
        } else {
            // Create new account
            AccountJpaEntity jpaEntity = AccountMapper.toJpa(account);
            AccountJpaEntity savedEntity = jpaRepository.save(jpaEntity);
            return AccountMapper.toDomain(savedEntity);
        }
    }

    @Override
    public void deleteById(String accountId) {
        jpaRepository.deleteById(accountId);
    }

    @Override
    public boolean existsById(String accountId) {
        return jpaRepository.existsById(accountId);
    }

    @Override
    public List<Account> findByCustomerId(String customerId) {
        return jpaRepository.findByCustomerId(customerId)
                .stream()
                .map(AccountMapper::toDomain)
                .collect(Collectors.toList());
    }



    // Implementación simulada para persistencia de eventos de dominio
    private void saveDomainEvents(String accountId, List<DomainEvent> events) {
        // En una aplicación real, esto interactuaría con una base de datos o un bus de mensajes.
        // Aquí, solo los almacenamos en un mapa en memoria y los imprimimos.
        String key = accountId != null ? accountId : "global";
        domainEventsStore.computeIfAbsent(key, k -> new ArrayList<>()).addAll(events);
        System.out.println("--- SIMULATED DOMAIN EVENT PERSISTENCE ---");
        for (DomainEvent event : events) {
            System.out.println("Account " + key + " - Saved Event: " + event.getEventType() + " (ID: " + event.getEventId() + ")");
        }
        System.out.println("-----------------------------------------");
    }

    @Override
    public void saveDomainEvent(DomainEvent event) {
        // Este método se llamaría si se quisiera guardar un evento individualmente.
        // En nuestro flujo actual, usamos saveDomainEvents para lotes.
        saveDomainEvents("global", Collections.singletonList(event)); // Simulación
    }

    @Override
    public List<DomainEvent> getDomainEvents(String accountId) {
        // Recupera eventos de la tienda en memoria (simulación)
        return domainEventsStore.getOrDefault(accountId, Collections.emptyList());
    }
}
