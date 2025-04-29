package com.enf.batch.writer;

import com.enf.domain.entity.LetterStatusEntity;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnansweredLettersWriter implements ItemWriter<LetterStatusEntity> {

  private final RestTemplate restTemplate;

  @Value("${spring.module-api.notification-url}")
  private String notificationUrl;

  @Value("${spring.module-api.transfer-letter-url}")
  private String transferLetterUrl;

  @Override
  public void write(Chunk<? extends LetterStatusEntity> chunk) throws Exception {
    if (chunk.getItems().isEmpty()) {
      log.info("답장을 하지 않은 멘토가 없습니다.");
      return;
    }

    for(LetterStatusEntity letterStatus : chunk) {

      if (letterStatus.getMentor().getRole().getRoleName().equals("ADMIN")) {
        return;
      }

      LocalDate letterDate = letterStatus.getCreateAt().toLocalDate();
      LocalDate currentDate = LocalDate.now();

      // 현재 날짜와의 차이 계산
      int difference = (int) ChronoUnit.DAYS.between(letterDate, currentDate);

      if (difference == 3 || difference == 6) {
        sendNotification(letterStatus);
      } else if (difference == 4) {
        transferLetter(letterStatus, 1L);
      } else if (difference == 7) {
        transferLetter(letterStatus, 2L);
      }

    }
  }

  private void transferLetter(LetterStatusEntity letterStatus, Long transferSeq) {
    String url = transferLetterUrl
        + "?letterStatusSeq=" + letterStatus.getLetterStatusSeq()
        + "&transferSeq=" + transferSeq;

    try {
      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
      System.out.println(response.getStatusCode());
      if (response.getStatusCode().equals(HttpStatus.OK)) {
        log.info("상태 코드: {}, 편지 넘기기 성공: {}", response.getStatusCode(), response.getBody());
      } else {
        log.error("편지 넘기기 실패: {}", response.getStatusCode());
      }
    } catch (Exception e) {
      log.error("편지 넘기기 중 오류 발생: {}", e.getMessage(), e);
    }
  }

  private void sendNotification(LetterStatusEntity letterStatus) {
    String url = notificationUrl + "?letterStatusSeq=" + letterStatus.getLetterStatusSeq();

    try {
      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
      if (response.getStatusCode().equals(HttpStatus.OK)) {
        log.info("상태 코드: {}, 알림 전송 성공 {}", response.getStatusCode(), response.getBody());
      } else {
        log.error("알림 전송 실패: {}", response.getStatusCode());
      }
    } catch (Exception e) {
      log.error("알림 전송 중 오류 발생: {}", e.getMessage(), e);
    }
  }


}
