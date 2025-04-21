package com.enf.batch.writer;

import com.enf.domain.entity.RoleEntity;
import com.enf.domain.entity.UserEntity;
import com.enf.domain.model.type.UrlType;
import com.enf.domain.repository.RoleRepository;
import com.enf.domain.repository.UserRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalUserWriter implements ItemWriter<UserEntity> {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  @Value("${spring.kakao.admin-key}")
  private String adminKey;

  @Override
  public void write(Chunk<? extends UserEntity> chunk) throws Exception {
    if (chunk.getItems().isEmpty()) {
      log.info("처리할 회원탈퇴 사용자가 없습니다.");
      return;
    }

    RoleEntity role = roleRepository.findByRoleName("WITHDRAWAL");
    String nickname = "떠나간 새";
    for (UserEntity user : chunk.getItems()) {

      unlinkUser(user);
      userRepository.withdrawal(user.getUserSeq(), role, nickname);
    }

  }


  public void unlinkUser(UserEntity user) {
    // HTTP 헤더 설정
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "KakaoAK " + adminKey);
    headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

    // 요청 바디 설정
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("target_id_type", "user_id");
    body.add("target_id", user.getProviderId());

    // HTTP 요청 객체 생성
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

    // RestTemplate 초기화
    RestTemplate restTemplate = new RestTemplate();

    // 요청 전송
    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        UrlType.KAKAO_UNLINK_URL.getUrl(),
        HttpMethod.POST,
        request,
        new ParameterizedTypeReference<>() {}
    );

    // 응답 처리
    if (response.getStatusCode() == HttpStatus.OK) {
      log.info("회원 탈퇴 성공: {}", user.getNickname());
    } else {
      log.error("회원 탈퇴 실패: {}", response.getStatusCode());
    }
  }
}
