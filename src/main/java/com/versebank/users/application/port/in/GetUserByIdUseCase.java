package com.versebank.users.application.port.in;

import com.versebank.users.domain.User;
import com.versebank.users.domain.UserId;
import java.util.Optional;

public interface GetUserByIdUseCase {
    Optional<User> getUser(UserId userId);
}
