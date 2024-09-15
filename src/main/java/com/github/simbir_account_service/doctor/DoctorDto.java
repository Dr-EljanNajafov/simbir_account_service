package com.github.simbir_account_service.doctor;

public record DoctorDto(
        Long id,
        String lastName,
        String firstName,
        String specialty
) {
}
