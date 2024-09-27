package com.github.simbir_account_service.entity.account.default_loader;

import com.github.simbir_account_service.entity.account.Role;
import com.github.simbir_account_service.entity.account.Account;
import com.github.simbir_account_service.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserLoader {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    // Метод, вызываемый при инициализации приложения
    @PostConstruct
    public void loadDefaultUser() {
        Optional<Account> userAccount = accountRepository.findByUsername("user");

        // Если пользователь с именем "user" нет в БД, создаем его
        if (userAccount.isEmpty()) {
            registerDefaultUser();
        }
    }

    // Логика создания пользователя по умолчанию
    private void registerDefaultUser() {
        Account user = Account.builder()
                .role(Role.user) // Роль пользователя
                .username("user") // Логин пользователя
                .firstName("User") // Имя пользователя
                .lastName("User") // Фамилия пользователя
                .password(passwordEncoder.encode("user")) // Пароль пользователя (нужно заменить на безопасный)
                .build();

        accountRepository.save(user); // Сохраняем пользователя в БД
    }
}
