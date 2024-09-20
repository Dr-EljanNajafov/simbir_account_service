package com.github.simbir_account_service.controller;

import com.github.simbir_account_service.account.request.AuthenticationRequest;
import com.github.simbir_account_service.account.request.RefreshRequest;
import com.github.simbir_account_service.account.request.RegisterRequest;
import com.github.simbir_account_service.account.request.UpdateRequest;
import com.github.simbir_account_service.account.response.AuthenticationResponse;
import com.github.simbir_account_service.account.response.RefreshResponse;
import com.github.simbir_account_service.auth.jwt.JwtService;
import com.github.simbir_account_service.dto.AccountDto;
import com.github.simbir_account_service.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Authentication")
public class AccountController {
    private final AccountService accountService;
    private final JwtService jwtService;


    @Operation(summary = "Получение данных о текущем аккаунте")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/Me")
    public AccountDto me(HttpServletRequest request) {
        return jwtService.accessUser(request, accountService::accountInfo);
    }

    @Operation(summary = "Получение новой пары jwt пользователя")
    @PostMapping("/SingIn")
    public AuthenticationResponse singIn(@RequestBody AuthenticationRequest request) {
        return accountService.signIn(request);
    }

    @Operation(summary = "Регистрация нового аккаунта")
    @PostMapping("/SingUp")
    public AuthenticationResponse singUp(@RequestBody RegisterRequest request) {
        return accountService.signUp(request);
    }

    @Operation(summary = "Обновление своего аккаунта")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/Update")
    public AuthenticationResponse update(HttpServletRequest request, @RequestBody UpdateRequest updateRequest) {
        // Получаем имя пользователя из текущего токена
        return jwtService.accessUser(request, username -> {
            // Обновляем аккаунт и получаем ответ
            AuthenticationResponse response = accountService.update(username, updateRequest);

            // Инвалидация старого токена, если пароль был обновлен
            if (!response.getToken().isEmpty()) {
                Optional<String> oldToken = jwtService.token(request);
                oldToken.ifPresent(jwtService::blacklistedToken);
            }

            // Возвращаем новый токен
            return response;
        });
    }

    @Operation(summary = "Выход из аккаунта")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/SignOut")
    public ResponseEntity<String> signOut(HttpServletRequest request) {
        // Получаем текущий токен из запроса
        Optional<String> token = jwtService.token(request);

        if (token.isPresent()) {
            // Инвалидация токена
            jwtService.blacklistedToken(token.get());
            return ResponseEntity.ok("Вы успешно вышли из аккаунта.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Не удалось завершить сеанс. Токен не найден.");
        }
    }

    @Operation(summary = "Интроспекция токена")
    @GetMapping("/Validate")
    public ResponseEntity<?> validateToken(@RequestParam String accessToken) {
        boolean isValid = jwtService.isTokenValid(accessToken);
        if (isValid) {
            return ResponseEntity.ok("Токен валиден");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Токен недействителен");
        }
    }

    @Operation(summary = "Обновление пары токенов")
    @PostMapping("/Refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@RequestBody RefreshRequest request) {
        // Обработка токена обновления
        String refreshToken = request.getRefreshToken();
        // Логика для обновления токенов
        RefreshResponse response = jwtService.refreshTokens(refreshToken);
        return ResponseEntity.ok(response);
    }
}
