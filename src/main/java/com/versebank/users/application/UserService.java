package com.versebank.users.application;

import com.versebank.accounts.application.port.in.AccountQueryPort;
import com.versebank.accounts.application.port.in.AccountSummary;
import com.versebank.users.application.port.in.CreateUserUseCase;
import com.versebank.users.application.port.in.GetUserByIdUseCase;
import com.versebank.users.application.port.in.LinkAccountToUserUseCase;
import com.versebank.users.domain.Email;
import com.versebank.users.domain.User;
import com.versebank.users.domain.UserId;
import com.versebank.users.domain.UserRepository;
import com.versebank.users.domain.exceptions.UserAlreadyExistsException;
import com.versebank.users.infrastructure.web.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UserService implements CreateUserUseCase, GetUserByIdUseCase, LinkAccountToUserUseCase {

    private final UserRepository userRepository;
    private final AccountQueryPort accountQueryPort;

    public UserService(UserRepository userRepository, AccountQueryPort accountQueryPort) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.accountQueryPort = Objects.requireNonNull(accountQueryPort);
    }

    @Override
    public UserId createUser(String name, String email) throws UserAlreadyExistsException {
        Email emailVO = Email.of(email);
        if (userRepository.existsByEmail(emailVO)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }
        User newUser = User.create(name, email);
        User savedUser = userRepository.save(newUser);
        return savedUser.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUser(UserId userId) {
        return userRepository.findById(userId);
    }

    @Override
    public void linkAccountToUser(UserId userId, String accountId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId.getValue()));
        
        // Verify account exists through the accounts slice
        Optional<AccountSummary> account = accountQueryPort.findByAccountId(accountId);
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }
        
        // Verify the account belongs to this user
        if (!account.get().getCustomerId().equals(userId.getValue())) {
            throw new IllegalArgumentException("Account does not belong to user: " + userId.getValue());
        }
        
        user.addAccount(accountId);
        userRepository.save(user);
    }

    @Override
    public void unlinkAccountFromUser(UserId userId, String accountId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId.getValue()));
        
        user.removeAccount(accountId);
        userRepository.save(user);
    }

    /**
     * Obtiene un usuario con sus cuentas asociadas
     */
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserWithAccounts(UserId userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    List<AccountSummary> accounts = accountQueryPort.findAccountsByCustomerId(userId.getValue());
                    return new UserResponse(
                        user.getId().getValue(),
                        user.getName(),
                        user.getEmail().getValue(),
                        accounts
                    );
                });
    }
}
