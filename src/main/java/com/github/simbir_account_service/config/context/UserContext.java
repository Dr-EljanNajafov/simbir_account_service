package com.github.simbir_account_service.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {
    private String token;

    // Геттеры и сеттеры
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void clear() {
        this.token = null;
    }
}