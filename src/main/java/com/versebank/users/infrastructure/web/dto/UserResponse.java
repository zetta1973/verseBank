package com.versebank.users.infrastructure.web.dto;

import com.versebank.accounts.application.port.in.AccountSummary;
import java.util.List;

public class UserResponse {
    private String userId;
    private String name;
    private String email;
    private List<AccountSummary> accounts;

    public UserResponse(String userId, String name, String email, List<AccountSummary> accounts) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.accounts = accounts;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<AccountSummary> getAccounts() { return accounts; }
}
