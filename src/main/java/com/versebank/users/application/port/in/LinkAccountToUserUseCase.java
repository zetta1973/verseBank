package com.versebank.users.application.port.in;

import com.versebank.users.domain.UserId;

public interface LinkAccountToUserUseCase {
    void linkAccountToUser(UserId userId, String accountId);
    void unlinkAccountFromUser(UserId userId, String accountId);
}
