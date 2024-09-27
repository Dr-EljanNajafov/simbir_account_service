package com.github.simbir_account_service.repository;

import com.github.simbir_account_service.entity.blacklist.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, String> {
}