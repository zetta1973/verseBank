package com.versebank.users.domain;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId userId);
    Optional<User> findByEmail(Email email);
    User save(User user);
    boolean existsById(UserId userId);
    boolean existsByEmail(Email email);
    void deleteById(UserId userId);
}
