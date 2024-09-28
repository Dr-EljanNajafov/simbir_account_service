package com.github.simbir_account_service.controller;

import com.github.simbir_account_service.service.AdminService;
import com.github.simbir_account_service.entity.account.request.GetAccountRequest;
import com.github.simbir_account_service.entity.account.request.RegisterByAdminRequest;
import com.github.simbir_account_service.entity.account.request.UpdateByAdminRequest;
import com.github.simbir_account_service.dto.AccountDto;
import com.github.simbir_account_service.entity.account.Account;
import com.github.simbir_account_service.repository.AccountRepository;
import com.github.simbir_account_service.service.AdminAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Accounts")
public class AdminAccountController {

    private final AdminAccountService adminAccountService;
    private final AdminService adminService;
    private final AccountRepository repository;


    @Operation(summary = "Получение списка всех аккаунтов")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts(GetAccountRequest getAccountRequest, HttpServletRequest request) {
        List<AccountDto> accounts = adminService.checkAdmin(request, userId -> adminAccountService.getUsers(getAccountRequest));
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Получение аккаунта по id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable long id, HttpServletRequest request) {
        AccountDto account = adminService.checkAdminDoctorManager(request, userId -> adminAccountService.getUserById(id));
        return ResponseEntity.ok(account);
    }

    @Operation(summary = "Создание администратором нового пользователя")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public AccountDto registerUser(HttpServletRequest request, @RequestBody RegisterByAdminRequest registerByAdminRequest) {
        return adminService.checkAdmin(request, userId -> adminAccountService.registerUser(registerByAdminRequest));
    }

    @Operation(summary = "Изменение администратором аккаунта по id ")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public AccountDto updateUser(HttpServletRequest request, @PathVariable long id, @RequestBody UpdateByAdminRequest updateByAdminRequest) {
        // Получаем пользователя по id
        Account user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        // Извлекаем username
        String username = user.getUsername();

        // Проверяем, является ли текущий пользователь администратором
        return adminService.checkAdmin(request, userId -> adminAccountService.updateAccount(username, updateByAdminRequest));
    }

    @Operation(summary = "Изменение администратором аккаунта по id ")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(HttpServletRequest request, @PathVariable long id) {
        // Проверяем права администратора
        adminService.checkAdmin(request, userId -> {
            adminAccountService.deleteAccount(id);
            return null; // Не нужно возвращать значение
        });
        // Возвращаем статус 200 OK и сообщение
        return ResponseEntity.ok("User with ID " + id + " has been deleted.");
    }
}
