package com.github.simbir_account_service.doctor.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetDoctorRequest {
    private String nameFilter; // Фильтр имени
    private int from;
    private int count;
}