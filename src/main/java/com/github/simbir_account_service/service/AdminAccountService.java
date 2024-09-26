package com.github.simbir_account_service.service;

import com.github.simbir_account_service.account.*;
import com.github.simbir_account_service.admin.account.request.GetAccountRequest;
import com.github.simbir_account_service.admin.account.request.RegisterByAdminRequest;
import com.github.simbir_account_service.admin.account.request.UpdateByAdminRequest;
import com.github.simbir_account_service.dto.AccountDto;
import com.github.simbir_account_service.dto.mapper.AccountDtoMapper;
import com.github.simbir_account_service.model.Account;
import com.github.simbir_account_service.repository.AccountRepository;
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
    private final AccountDtoMapper accountDtoMapper;

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

    public AccountDto getUserById(Long id) {
        Account user = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return accountDtoMapper.apply(user);
    }

    public AccountDto registerUser(RegisterByAdminRequest request) {
        accountService.checkUsername(request.getUsername());

        Role role = Role.user; // Значение по умолчанию

        if (request.getRoles() != null) {
            if (request.getRoles().contains("admin")) {
                role = Role.admin;
            } else if (request.getRoles().contains("doctor")) {
                role = Role.doctor;
            } else if (request.getRoles().contains("manager")) {
                role = Role.manager;
            }
        }

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

        if (!request.getPassword().isEmpty()) {
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
}
