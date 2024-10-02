package com.github.simbir_account_service.repository;

import com.github.simbir_account_service.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Account, Long> {

    @Query("SELECT d FROM Account d WHERE " +
            "LOWER(CONCAT(d.firstName, ' ', d.lastName)) LIKE LOWER(CONCAT('%', :nameFilter, '%')) AND d.role = 'doctor'")
    List<Account> findDoctorByFullNameContainingIgnoreCase(String nameFilter);

    @Query("SELECT d FROM Account d WHERE d.id = :id AND d.role = 'doctor'")

    Optional<Account> findDoctorById(Long id);
}
