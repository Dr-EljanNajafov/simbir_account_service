package com.github.simbir_account_service.service;

import com.github.simbir_account_service.auth.jwt.JwtService;
import com.github.simbir_account_service.entity.account.Account;
import com.github.simbir_account_service.repository.AccountRepository;
import com.github.simbir_account_service.entity.account.Role;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final JwtService jwtService;
    private final AccountRepository accountRepository;


    // Версия без возврата результата (void)
    public void checkAdminVoid(HttpServletRequest request, Consumer<String> adminConsumer) {
        checkAdmin(request, username -> {
            adminConsumer.accept(username);
            return null;  // Так как это версия без возврата значения
        });
    }

    // Версия без возврата результата (void)
    public void checkAdminDoctorManagerVoid(HttpServletRequest request, Consumer<String> adminConsumer) {
        checkAdminDoctorManager(request, username -> {
            adminConsumer.accept(username);
            return null;  // Так как это версия без возврата значения
        });
    }

    // Версия с возвратом результата
    public <T> T checkAdmin(HttpServletRequest request, Function<String, T> adminConsumer) {
        // Используем accessUser для извлечения имени пользователя (username)
        return jwtService.accessUser(request, username -> {
            // Находим пользователя по username
            Account user = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            // Проверяем роль пользователя
            if (user.getRole() != Role.admin) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can access this endpoint");
            }

            // Если проверка прошла, применяем переданную функцию adminConsumer
            return adminConsumer.apply(username);
        });
    }

    public <T> T checkAdminDoctorManager(HttpServletRequest request, Function<String, T> adminConsumer) {
        return jwtService.accessUser(request, username -> {
            // Находим пользователя по username
            Account user = accountRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            // Проверяем, что роль пользователя - admin, doctor или manager
            if (!EnumSet.of(Role.admin, Role.doctor, Role.manager, Role.user).contains(user.getRole())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Only admins, doctors, or managers can access this endpoint.");
            }

            // Если проверка прошла, применяем переданную функцию adminConsumer
            return adminConsumer.apply(username);
        });
    }
}