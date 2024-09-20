package com.github.simbir_account_service.repository;

import com.github.simbir_account_service.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d WHERE " +
            "LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :nameFilter, '%'))")
    List<Doctor> findByFullNameContainingIgnoreCase(String nameFilter);

    Optional<Doctor> findById(Long id);
}
