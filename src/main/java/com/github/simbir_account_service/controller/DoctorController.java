package com.github.simbir_account_service.controller;

import com.github.simbir_account_service.doctor.request.GetDoctorRequest;
import com.github.simbir_account_service.model.Doctor;
import com.github.simbir_account_service.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Doctors")
public class DoctorController {

    private final DoctorService doctorService;

    @Operation(summary = "Получение списка докторов")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<List<Doctor>> getDoctors(
            @RequestParam(required = false, defaultValue = "") String nameFilter,
            @RequestParam int from,
            @RequestParam int count
    ) {
        GetDoctorRequest getDoctorRequest = GetDoctorRequest.builder()
                .nameFilter(nameFilter)
                .from(from)
                .count(count)
                .build();

        List<Doctor> doctors = doctorService.getDoctors(getDoctorRequest);
        return ResponseEntity.ok(doctors);
    }

    @Operation(summary = "Получение информации о докторе по Id")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(
            @PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }
}