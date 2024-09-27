package com.github.simbir_account_service.entity.account.default_loader;

import com.github.simbir_account_service.entity.account.Account;
import com.github.simbir_account_service.entity.account.Role;
import com.github.simbir_account_service.repository.AccountRepository;
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
