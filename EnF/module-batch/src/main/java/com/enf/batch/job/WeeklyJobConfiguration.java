package com.enf.batch.job;

import com.enf.batch.reader.UserQuotaReader;
import com.enf.batch.writer.UserQuotaWriter;
import com.enf.domain.entity.UserEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
@RequiredArgsConstructor
public class WeeklyJobConfiguration {

  private final EntityManagerFactory entityManagerFactory;

  private final UserQuotaReader userQuotaReader;
  private final UserQuotaWriter userQuotaWriter;

  @Bean
  public Job weeklyUpdateJob(JobRepository jobRepository){
    return new JobBuilder("weeklyUpdateJob", jobRepository)
        .start(weeklyUpdateStep(jobRepository))
        .build();
  }

  @Bean
  public Step weeklyUpdateStep(JobRepository jobRepository) {
    return new StepBuilder("withdrawalUserStep", jobRepository)
        .<UserEntity, UserEntity>chunk(100, new JpaTransactionManager(entityManagerFactory))
        .reader(new ListItemReader<>(userQuotaReader.getAllUser()))
        .writer(userQuotaWriter)
        .build();
  }

}
