package com.versebank.users.infrastructure.persistence;

import com.versebank.users.domain.Email;
import com.versebank.users.domain.User;
import com.versebank.users.domain.UserId;

import java.util.ArrayList;

public class UserMapper {

    public static UserJpaEntity toJpa(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId().getValue());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail().getValue());
        entity.setAccountIds(new ArrayList<>(user.getAccountIds()));
        return entity;
    }

    public static User toDomain(UserJpaEntity entity) {
        User user = new User(
            UserId.fromString(entity.getId()),
            entity.getName(),
            Email.of(entity.getEmail())
        );
        
        if (entity.getAccountIds() != null) {
            entity.getAccountIds().forEach(user::addAccount);
        }
        
        return user;
    }
}
