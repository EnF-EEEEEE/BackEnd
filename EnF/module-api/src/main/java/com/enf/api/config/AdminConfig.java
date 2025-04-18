package com.enf.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AdminConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // RestTemplate 빈 등록
    }
}
