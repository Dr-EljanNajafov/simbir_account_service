package com.github.simbir_account_service.config;

import com.github.simbir_account_service.client.FeignUserInterceptor;
import com.github.simbir_account_service.client.context.UserContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

@Configuration
public class FeignConfig {

    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext) {
        return new FeignUserInterceptor(userContext);
    }
}