package com.enf.batch.config;

import org.springframework.batch.core.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchConfig {

  private final JobLauncher jobLauncher;
  private final Job dailyUpdateJob;
  private final Job weeklyUpdateJob;

  // 임시 테스트 용 추후 jenkins 적용할 예정
  @Scheduled(cron = "0 */1 * * * *")
  public void dailyJob() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("timestamp", String.valueOf(System.currentTimeMillis()))
        .toJobParameters();

    jobLauncher.run(dailyUpdateJob, jobParameters);
  }

  @Scheduled(cron = "0 */1 * * * *")
  public void weeklyJob() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder()
        .addString("timestamp", String.valueOf(System.currentTimeMillis()))
        .toJobParameters();

    jobLauncher.run(weeklyUpdateJob, jobParameters);
  }

}
