package com.github.simbir_account_service.dto.mapper;

import com.github.simbir_account_service.entity.doctor.Doctor;
import com.github.simbir_account_service.dto.DoctorDto;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class DoctorDtoMapper implements Function<Doctor, DoctorDto> {
    @Override
    public DoctorDto apply(Doctor doctor) {
        return new DoctorDto(
                doctor.getId(),
                doctor.getLastName(),
                doctor.getFirstName(),
                doctor.getSpecialty()
        );
    }
}
