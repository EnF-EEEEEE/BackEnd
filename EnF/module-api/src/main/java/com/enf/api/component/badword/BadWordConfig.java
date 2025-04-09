package com.enf.api.component.badword;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class BadWordConfig {

    @Bean
    public BadWordFiltering badWordFiltering() {
        return new BadWordFiltering();
    }
}
