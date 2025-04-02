package com.enf.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.enf.domain.repository")
@EnableRedisRepositories(basePackages = "com.enf.domain.repository")
@EntityScan(basePackages = "com.enf.domain")
@ComponentScan(basePackages = "com.enf")
public class APIApplication {

  public static void main(String[] args) {
    SpringApplication.run(APIApplication.class, args);
  }

}
