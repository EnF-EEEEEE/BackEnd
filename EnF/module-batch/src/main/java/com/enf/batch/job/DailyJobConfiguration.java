package com.enf.batch.job;

import com.enf.batch.reader.UnansweredLettersReader;
import com.enf.batch.reader.WithdrawalUserReader;
import com.enf.batch.writer.UnansweredLettersWriter;
import com.enf.batch.writer.WithdrawalUserWriter;
import com.enf.domain.entity.LetterStatusEntity;
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
public class DailyJobConfiguration {

  private final EntityManagerFactory entityManagerFactory;

  private final WithdrawalUserReader withdrawalUserReader;
  private final WithdrawalUserWriter withdrawalUserWriter;

  private final UnansweredLettersReader unansweredLettersReader;
  private final UnansweredLettersWriter unansweredLettersWriter;

  @Bean
  public Job dailyUpdateJob(JobRepository jobRepository){
    return new JobBuilder("dailyUpdateJob", jobRepository)
        .start(withdrawalUserStep(jobRepository))
        .next(letterStep(jobRepository))
        .build();
  }

  @Bean
  public Step withdrawalUserStep(JobRepository jobRepository) {
    return new StepBuilder("withdrawalUserStep", jobRepository)
        .<UserEntity, UserEntity>chunk(100, new JpaTransactionManager(entityManagerFactory))
        .reader(new ListItemReader<>(withdrawalUserReader.getWithdrawalUsers()))
        .writer(withdrawalUserWriter)
        .build();
  }

  @Bean
  public Step letterStep(JobRepository jobRepository) {
    return new StepBuilder("letterStep", jobRepository)
        .<LetterStatusEntity, LetterStatusEntity>chunk(100, new JpaTransactionManager(entityManagerFactory))
        .reader(new ListItemReader<>(unansweredLettersReader.getUnansweredLetters()))
        .writer(unansweredLettersWriter)
        .build();
  }

}
