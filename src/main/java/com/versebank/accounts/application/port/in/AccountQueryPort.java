package com.versebank.accounts.application.port.in;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de consulta que permite a otros slices consultar información de cuentas
 * sin acceder directamente a la implementación.
 */
public interface AccountQueryPort {
    Optional<AccountSummary> findByAccountId(String accountId);
    List<AccountSummary> findAccountsByCustomerId(String customerId);
}
