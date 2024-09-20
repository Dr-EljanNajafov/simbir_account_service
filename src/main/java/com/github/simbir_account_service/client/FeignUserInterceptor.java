package com.github.simbir_account_service.client;

import com.github.simbir_account_service.config.context.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {
    private final UserContext userContext;

    @Override
    public void apply(RequestTemplate template) {
        // Извлечение токена из UserContext
        String token = userContext.getToken();
        if (token != null) {
            // Добавление токена в заголовок Authorization
            template.header("Authorization", "Bearer " + token);
        }
    }
}
