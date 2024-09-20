package com.github.simbir_account_service.service;

import com.github.simbir_account_service.repository.AccountRepository;
import com.github.simbir_account_service.account.Role;
import com.github.simbir_account_service.account.request.AuthenticationRequest;
import com.github.simbir_account_service.account.request.RegisterRequest;
import com.github.simbir_account_service.account.request.UpdateRequest;
import com.github.simbir_account_service.account.response.AuthenticationResponse;
import com.github.simbir_account_service.auth.jwt.JwtService;
import com.github.simbir_account_service.blacklist.JwtBlacklistRepository;
import com.github.simbir_account_service.dto.AccountDto;
import com.github.simbir_account_service.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JwtBlacklistRepository jwtBlacklistRepository;

    // Retrieve user info by username
    public AccountDto accountInfo(String username) {

        // Get the current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) authentication.getCredentials();

        Account user = user(username);
        return new AccountDto(
                user.getId(),
                user.getUsername(),
                user.getLastName(),
                user.getFirstName(),
                user.getRole()
        );
    }

    // User sign-in (authentication)
    public AuthenticationResponse signIn(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        Account user = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        String jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
    }

    // User sign-up (registration)
    public AuthenticationResponse signUp(RegisterRequest request) {
        checkUsername(request.getUsername());

        Account user = Account.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.user)
                .build();

        accountRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    // Update user information
    public AuthenticationResponse update(String username, UpdateRequest request) {
        // Получаем текущего пользователя
        Account user = user(username);

        // Флаг для проверки, был ли обновлен пароль
        boolean isPasswordUpdated = false;

        // Обновляем поля пользователя
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            isPasswordUpdated = true;
        }
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        accountRepository.save(user);

        // Если пароль обновлен, возвращаем новый токен
        if (isPasswordUpdated) {
            return signIn(new AuthenticationRequest(username, request.getPassword()));
        }

        // Если пароль не обновлен, возвращаем старый токен
        return new AuthenticationResponse(jwtService.generateToken(user));
    }

    // Retrieve user by username
    public Account user(String username) {
        Account user = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with username %s doesn't exist".formatted(username)
                ));

        return user;
    }

    // Check if username is already in use
    public void checkUsername(String username, String username1) {
        Optional<Account> userOptional = accountRepository.findByUsername(username);
        if (userOptional.isPresent() && !userOptional.get().getUsername().equals(username1)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username '%s' is already in use".formatted(username)
            );
        }
    }

    public void checkUsername(String username) {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username '%s' is already in use".formatted(username)
            );
        }
    }
}
