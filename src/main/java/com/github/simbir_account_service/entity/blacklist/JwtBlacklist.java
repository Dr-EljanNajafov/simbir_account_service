package com.github.simbir_account_service.entity.blacklist;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "jwt_blacklist")
@AllArgsConstructor
@NoArgsConstructor
public class JwtBlacklist {
    @Id
    private String token;
}