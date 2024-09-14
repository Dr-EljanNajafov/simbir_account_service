package com.github.simbir_account_service.blacklist;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "jwt_blacklist")
@AllArgsConstructor
@NoArgsConstructor
public class JwtBlacklist {
    @Id
    private String token;
}