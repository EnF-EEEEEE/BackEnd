package com.enf.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.enf.domain") // domain 모듈의 Entity 스캔
@EnableJpaRepositories(basePackages = "com.enf.domain.repository") // domain 모듈의 Repository 스캔
@ComponentScan(basePackages = {"com.enf.email", "com.enf.domain"}) // 이메일 모듈과 도메인 모듈 컴포넌트 스캔
public class EmailApplication {

  public static void main(String[] args) {
    SpringApplication.run(EmailApplication.class, args);
  }

}
