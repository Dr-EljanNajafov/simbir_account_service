package com.github.simbir_account_service.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(Long userId){
        userIdHolder.set(userId);
    }

    public long getUserId() {
        return userIdHolder.get();
    }

    public void clear() {
        userIdHolder.remove();
    }
}