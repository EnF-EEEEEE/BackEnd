package com.enf.api.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Value("${api.key.name}")
    private String apiKeyName;

    @Value("${api.key.value}")
    private String apiKey;

    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(apiKeyName, apiKey);
        };
    }
}
