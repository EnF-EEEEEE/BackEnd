package com.enf.batch.writer;

import com.enf.domain.entity.RoleEntity;
import com.enf.domain.entity.UserEntity;
import com.enf.domain.repository.RoleRepository;
import com.enf.domain.repository.UserRepository;
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
public class WithdrawalUserWriter implements ItemWriter<UserEntity> {

  private final RestTemplate restTemplate;

  @Value("${spring.module-api.unlink-url}")
  private String unlinkUrl;

  @Override
  public void write(Chunk<? extends UserEntity> chunk) throws Exception {
    if (chunk.getItems().isEmpty()) {
      log.info("처리할 회원탈퇴 사용자가 없습니다.");
      return;
    }

    for (UserEntity user : chunk.getItems()) {
      log.info("탈퇴 사용자 닉네임 : {}", user.getNickname());
      unlinkUser(user);
    }

  }


  private void unlinkUser(UserEntity user) {
    String url = unlinkUrl + "?userSeq=" + user.getUserSeq();

    try {
      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
      if (response.getStatusCode().equals(HttpStatus.OK)) {
        log.info("상태 코드: {}, 회원 탈퇴 성공: {}", response.getStatusCode(), response.getBody());
      } else {
        log.error("회원 탈퇴 실패: {}", response.getStatusCode());
      }
    } catch (Exception e) {
      log.error("회원 탈퇴 중 오류 발생: {}", e.getMessage(), e);
    }
  }
}
