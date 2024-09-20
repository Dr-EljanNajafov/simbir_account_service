package com.github.simbir_account_service.dto;

public record DoctorDto(
        Long id,
        String lastName,
        String firstName,
        String specialty
) {
}
