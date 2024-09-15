package com.github.simbir_account_service.admin.account;

import com.github.simbir_account_service.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    public List<AccountDto> users(GetAccountRequest request) {
        if (request.getFrom() < 0 || request.getCount() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'from' and 'count' must be >= 0");
        }

        List<Account> users = accountRepository.findAll();
        List<Account> subList = users
                .subList(
                        Math.min(request.getFrom(), users.size()),
                        Math.min(request.getFrom() + request.getCount(), users.size()));

        return subList.stream()
                .map(account -> new AccountDto(
                        account.getId(),
                        account.getUsername(),
                        account.getLastName(),
                        account.getFirstName(),
                        account.getRole()))
                .collect(Collectors.toList());
    }

    public AccountDto registerUser(RegisterByAdminRequest request) {
        accountService.checkUsername(request.getUsername());

        Role role = (request.getRoles() != null && request.getRoles().contains("admin"))
                ? Role.admin : Role.user;

        Account user = Account.builder()
                .role(role)
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        accountRepository.save(user);
        return accountService.accountInfo(user.getUsername());
    }

    public AccountDto updateAccount(String username, UpdateByAdminRequest request) {
        accountService.checkUsername(request.getUsername(), username);

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Role role = request.getRoles().contains("admin") ? Role.admin : Role.user;
            account.setRole(role);
        }

        account.setUsername(request.getUsername());
        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        accountRepository.save(account);
        return accountService.accountInfo(account.getUsername());
    }

    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        accountRepository.delete(account);
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
    }
}
