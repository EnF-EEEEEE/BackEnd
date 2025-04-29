package com.enf.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchConfig implements ApplicationRunner {

  private final JobLauncher jobLauncher;
  private final Job dailyUpdateJob;
  private final Job weeklyUpdateJob;
  private final JobExecutionHolder jobExecutionHolder;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (args.containsOption("job.name")) {
      String jobName = args.getOptionValues("job.name").get(0);
      log.info("Job name >>> {}", jobName);

      JobParameters jobParameters = new JobParametersBuilder()
          .addLong("time", System.currentTimeMillis())
          .toJobParameters();

      JobExecution jobExecution = null;

      switch (jobName) {
        case "daily" -> {
          log.info("Daily Job 실행");
          jobExecution = jobLauncher.run(dailyUpdateJob, jobParameters);
        }
        case "weekly" -> {
          log.info("weekly Job 실행");
          jobExecution = jobLauncher.run(weeklyUpdateJob, jobParameters);
        }
        default -> log.warn("알 수 없는 작업: {}", jobName);
      }

      if (jobExecution != null) {
        log.info("작업 명 : '{}', 작업 결과 : {}", jobName, jobExecution.getStatus());
        jobExecutionHolder.setJobExecution(jobExecution);
      }
    } else {
      log.warn("No job name specified");
    }
  }

}
