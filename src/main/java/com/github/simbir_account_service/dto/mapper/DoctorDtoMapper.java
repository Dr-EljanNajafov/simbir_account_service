package com.github.simbir_account_service.dto.mapper;

import com.github.simbir_account_service.dto.DoctorDto;
import com.github.simbir_account_service.entity.account.Account;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class DoctorDtoMapper implements Function<Account, DoctorDto> {
    @Override
    public DoctorDto apply(Account doctor) {
        return new DoctorDto(
                doctor.getId(),
                doctor.getLastName(),
                doctor.getFirstName(),
                doctor.getRole()
        );
    }
}
