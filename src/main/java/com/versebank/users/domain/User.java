package com.versebank.users.domain;

import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class User {
    private final UserId id;
    private String name;
    private Email email;
    private final List<String> accountIds;

    public User(UserId id, String name, Email email) {
        this.id = Objects.requireNonNull(id, "UserId cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.accountIds = new ArrayList<>();
    }

    public static User create(String name, String email) {
        return new User(UserId.generate(), name, Email.of(email));
    }

    public void addAccount(String accountId) {
        if (accountId != null && !accountId.isEmpty() && !accountIds.contains(accountId)) {
            this.accountIds.add(accountId);
        }
    }
    
    public boolean removeAccount(String accountId) {
        return accountIds.remove(accountId);
    }

    // Getters
    public UserId getId() { return id; }
    public String getName() { return name; }
    public Email getEmail() { return email; }
    public List<String> getAccountIds() { return Collections.unmodifiableList(accountIds); }

    // Setters
    public void setName(String name) { 
        this.name = Objects.requireNonNull(name, "Name cannot be null"); 
    }
    public void setEmail(Email email) { 
        this.email = Objects.requireNonNull(email, "Email cannot be null"); 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
