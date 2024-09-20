package com.github.simbir_account_service.service;

import com.github.simbir_account_service.repository.DoctorRepository;
import com.github.simbir_account_service.doctor.request.GetDoctorRequest;
import com.github.simbir_account_service.model.Doctor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public List<Doctor> getDoctors(GetDoctorRequest request) {
        if (request.getFrom() < 0 || request.getCount() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'from' and 'count' must be >= 0");
        }

        List<Doctor> doctors = doctorRepository.findByFullNameContainingIgnoreCase(request.getNameFilter());

        int endIndex = Math.min(request.getFrom() + request.getCount(), doctors.size());
        return doctors.subList(Math.min(request.getFrom(), doctors.size()), endIndex);
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
    }
}