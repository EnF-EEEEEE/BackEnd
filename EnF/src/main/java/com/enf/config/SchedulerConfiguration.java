package com.enf.config;

import com.enf.component.KakaoAuthHandler;
import com.enf.component.facade.UserFacade;
import com.enf.entity.QuotaEntity;
import com.enf.entity.UserEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerConfiguration {

  private final UserFacade userFacade;
  private final KakaoAuthHandler kakaoAuthHandler;


  @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul") // 매주 월요일 00:00 실행 (한국 시간 기준)
  public void updateLetterQuota() {
    log.info("사용자 편지 할당량 초기화 작업 시작");

    List<QuotaEntity> quotas = userFacade.getQuotas();
    if (quotas == null || quotas.isEmpty()) {
      log.info("초기화할 사용자 할당량이 없음");
      return;
    }

    for (QuotaEntity quota : quotas) {
      boolean isMentee = quota.getUser().getRole().getRoleName().equals("MENTEE");
      userFacade.resetQuota(quota.getUser(), isMentee ? 4 : 7);
    }

    log.info("사용자 편지 할당량 초기화 작업 완료");
  }

  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul") // 매주 월요일 00:00 실행 (한국 시간 기준)
  public void withdrawal() {
    log.info("사용자 회원탈퇴 로직 구현 작업 시작");

    List<UserEntity> withdrawalPendingUsers = userFacade.getWithdrawalPendingUsers();
    if (withdrawalPendingUsers == null || withdrawalPendingUsers.isEmpty()) {
      log.info("회원탈퇴 할 사용자 없음");
      return;
    }

    for (UserEntity user : withdrawalPendingUsers) {
      kakaoAuthHandler.unlinkUser(user);
      userFacade.withdrawal(user);
    }

    log.info("사용자 회원탈퇴 작업 완료");
  }

}
