package com.github.simbir_account_service.admin.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateByAdminRequest {
    @NonNull
    private String lastName;
    @NonNull
    private String firstName;
    @NonNull
    private String username;
    @NonNull
    private String password;
    private List<String> roles;  // Список строк для ролей
}