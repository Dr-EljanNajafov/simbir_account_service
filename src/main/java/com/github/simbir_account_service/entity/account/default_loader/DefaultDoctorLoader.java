package com.github.simbir_account_service.entity.account.default_loader;

import com.github.simbir_account_service.entity.account.Role;
import com.github.simbir_account_service.entity.account.Account;
import com.github.simbir_account_service.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultDoctorLoader {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    // Метод, вызываемый при инициализации приложения
    @PostConstruct
    public void loadDefaultDoctor() {
        Optional<Account> doctorAccount = accountRepository.findByUsername("doctor");

        // Если доктора с именем "doctor" нет в БД, создаем его
        if (doctorAccount.isEmpty()) {
            registerDefaultDoctor();
        }
    }

    // Логика создания доктора по умолчанию
    private void registerDefaultDoctor() {
        Account doctor = Account.builder()
                .role(Role.doctor) // Роль доктора
                .username("doctor") // Логин доктора
                .firstName("Doctor") // Имя доктора
                .lastName("Doctor") // Фамилия доктора
                .password(passwordEncoder.encode("doctor")) // Пароль доктора (нужно заменить на безопасный)
                .build();

        accountRepository.save(doctor); // Сохраняем доктора в БД
    }
}
