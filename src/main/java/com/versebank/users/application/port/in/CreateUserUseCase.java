package com.versebank.users.application.port.in;

import com.versebank.users.domain.UserId;
import com.versebank.users.domain.exceptions.UserAlreadyExistsException;

public interface CreateUserUseCase {
    UserId createUser(String name, String email) throws UserAlreadyExistsException;
}
