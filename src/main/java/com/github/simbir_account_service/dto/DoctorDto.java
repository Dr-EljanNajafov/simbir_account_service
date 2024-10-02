package com.github.simbir_account_service.dto;

import com.github.simbir_account_service.entity.account.Role;

public record DoctorDto(
        Long id,
        String lastName,
        String firstName,
        Role role
) {
}
