package com.github.simbir_account_service.blacklist;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, String> {
}