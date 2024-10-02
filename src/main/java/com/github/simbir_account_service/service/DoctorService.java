package com.github.simbir_account_service.service;

import com.github.simbir_account_service.dto.DoctorDto;
import com.github.simbir_account_service.dto.mapper.DoctorDtoMapper;
import com.github.simbir_account_service.entity.account.Account;
import com.github.simbir_account_service.repository.DoctorRepository;
import com.github.simbir_account_service.entity.doctor.request.GetDoctorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorDtoMapper doctorDtoMapper;

    public List<DoctorDto> getDoctors(GetDoctorRequest request) {
        if (request.getFrom() < 0 || request.getCount() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'from' and 'count' must be >= 0");
        }

        List<Account> doctors = doctorRepository.findDoctorByFullNameContainingIgnoreCase(request.getNameFilter());

        int endIndex = Math.min(request.getFrom() + request.getCount(), doctors.size());
        List<Account> paginatedDoctors = doctors.subList(Math.min(request.getFrom(), doctors.size()), endIndex);

        // Convert each Account entity to DoctorDto
        return paginatedDoctors.stream()
                .map(doctorDtoMapper)
                .collect(Collectors.toList());
    }

    public DoctorDto getDoctorById(Long id) {
        Account doctor = doctorRepository.findDoctorById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        return doctorDtoMapper.apply(doctor);
    }
}
