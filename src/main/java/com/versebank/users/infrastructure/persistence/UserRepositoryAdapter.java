package com.versebank.users.infrastructure.persistence;

import com.versebank.users.domain.Email;
import com.versebank.users.domain.User;
import com.versebank.users.domain.UserId;
import com.versebank.users.domain.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return jpaRepository.findById(userId.getValue())
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.getValue())
                .map(UserMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserMapper.toJpa(user);
        UserJpaEntity savedEntity = jpaRepository.save(entity);
        return UserMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsById(UserId userId) {
        return jpaRepository.existsById(userId.getValue());
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.getValue());
    }

    @Override
    public void deleteById(UserId userId) {
        jpaRepository.deleteById(userId.getValue());
    }
}
