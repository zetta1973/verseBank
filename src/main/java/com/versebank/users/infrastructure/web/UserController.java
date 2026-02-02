package com.versebank.users.infrastructure.web;

import com.versebank.accounts.application.port.in.AccountQueryPort;
import com.versebank.accounts.application.port.in.AccountSummary;
import com.versebank.users.application.UserService;
import com.versebank.users.domain.User;
import com.versebank.users.domain.UserId;
import com.versebank.users.domain.exceptions.UserAlreadyExistsException;
import com.versebank.users.infrastructure.web.dto.CreateUserRequest;
import com.versebank.users.infrastructure.web.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AccountQueryPort accountQueryPort;

    public UserController(UserService userService, AccountQueryPort accountQueryPort) {
        this.userService = userService;
        this.accountQueryPort = accountQueryPort;
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest request) {
        try {
            UserId userId = userService.createUser(request.getName(), request.getEmail());
            return ResponseEntity.created(URI.create("/api/users/" + userId.getValue()))
                    .body(userId.getValue());
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userId) {
        return userService.getUserWithAccounts(UserId.fromString(userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/link-account/{accountId}")
    public ResponseEntity<String> linkAccount(@PathVariable String userId, @PathVariable String accountId) {
        try {
            userService.linkAccountToUser(UserId.fromString(userId), accountId);
            return ResponseEntity.ok("Account linked successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/link-account/{accountId}")
    public ResponseEntity<String> unlinkAccount(@PathVariable String userId, @PathVariable String accountId) {
        try {
            userService.unlinkAccountFromUser(UserId.fromString(userId), accountId);
            return ResponseEntity.ok("Account unlinked successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/accounts")
    public ResponseEntity<List<AccountSummary>> getUserAccounts(@PathVariable String userId) {
        return userService.getUser(UserId.fromString(userId))
                .map(user -> ResponseEntity.ok(accountQueryPort.findAccountsByCustomerId(userId)))
                .orElse(ResponseEntity.notFound().build());
    }
}
