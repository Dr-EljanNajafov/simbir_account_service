package com.github.simbir_account_service.admin.account.defaultLoader;

import com.github.simbir_account_service.account.Role;
import com.github.simbir_account_service.model.Account;
import com.github.simbir_account_service.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DefaultManagerLoader {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    // Метод, вызываемый при инициализации приложения
    @PostConstruct
    public void loadDefaultManager() {
        Optional<Account> managerAccount = accountRepository.findByUsername("manager");

        // Если менеджер с именем "manager" нет в БД, создаем его
        if (managerAccount.isEmpty()) {
            registerDefaultManager();
        }
    }

    // Логика создания менеджера по умолчанию
    private void registerDefaultManager() {
        Account manager = Account.builder()
                .role(Role.manager) // Роль менеджера
                .username("manager") // Логин менеджера
                .firstName("Manager") // Имя менеджера
                .lastName("Manager") // Фамилия менеджера
                .password(passwordEncoder.encode("manager")) // Пароль менеджера (нужно заменить на безопасный)
                .build();

        accountRepository.save(manager); // Сохраняем менеджера в БД
    }
}
