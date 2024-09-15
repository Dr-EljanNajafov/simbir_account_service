package com.github.simbir_account_service.account;

public record AccountDto(
        Long id,
        String username,
        String lastName,
        String firstName,
        Role role
) {
}