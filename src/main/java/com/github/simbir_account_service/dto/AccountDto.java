package com.github.simbir_account_service.dto;

import com.github.simbir_account_service.account.Role;

public record AccountDto(
        Long id,
        String username,
        String lastName,
        String firstName,
        Role role
) {
}