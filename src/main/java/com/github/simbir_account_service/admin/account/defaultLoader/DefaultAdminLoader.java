package com.github.simbir_account_service.admin.account.defaultLoader;

import com.github.simbir_account_service.account.*;
import com.github.simbir_account_service.model.Account;
import com.github.simbir_account_service.repository.AccountRepository;
import com.github.simbir_account_service.service.AccountService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultAdminLoader {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;

    // Метод, вызываемый при инициализации приложения
    @PostConstruct
    public void loadDefaultAdmin() {
        Optional<Account> adminAccount = accountRepository.findByUsername("admin");

        // Если администратора с именем "admin" нет в БД, создаем его
        if (adminAccount.isEmpty()) {
            registerDefaultAdmin();
        }
    }

    // Логика создания администратора по умолчанию
    private void registerDefaultAdmin() {
        Account admin = Account.builder()
                .role(Role.admin) // Роль администратора
                .username("admin") // Логин администратора
                .firstName("Default") // Имя администратора
                .lastName("Admin") // Фамилия администратора
                .password(passwordEncoder.encode("admin")) // Пароль администратора (нужно заменить на безопасный)
                .build();

        accountRepository.save(admin); // Сохраняем администратора в БД
    }
}
