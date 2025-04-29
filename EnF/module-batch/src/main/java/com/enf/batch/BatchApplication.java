package com.enf.batch;

import com.enf.batch.config.JobExecutionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
@EnableBatchProcessing
@EnableJpaRepositories(basePackages = "com.enf.domain.repository")
@EnableRedisRepositories(basePackages = "com.enf.domain.repository")
@EntityScan(basePackages = "com.enf.domain")
@ComponentScan(basePackages = "com.enf")
public class BatchApplication {

  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(BatchApplication.class, args);

    JobExecutionHolder jobExecutionHolder = context.getBean(JobExecutionHolder.class);
    JobExecution jobExecution = jobExecutionHolder.getJobExecution();

    if (jobExecution != null) {
      log.info("배치 작업이 종료되었습니다. 상태: {}", jobExecution.getStatus());
      int exitCode = jobExecution.getStatus().isUnsuccessful() ? 1 : 0;
      SpringApplication.exit(context, () -> exitCode);
    } else {
      log.warn("실행된 배치 작업을 찾을 수 없습니다.");
      SpringApplication.exit(context, () -> 1);
    }
  }

}
