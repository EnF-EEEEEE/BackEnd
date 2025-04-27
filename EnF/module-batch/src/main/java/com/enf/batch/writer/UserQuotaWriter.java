package com.enf.batch.writer;

import com.enf.domain.entity.UserEntity;
import com.enf.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserQuotaWriter implements ItemWriter<UserEntity> {

  private final UserRepository userRepository;

  @Override
  public void write(Chunk<? extends UserEntity> chunk) throws Exception {
    if (chunk.getItems().isEmpty()) {
      log.info("처리할 사용자가 없습니다.");
      return;
    }

    for (UserEntity user : chunk.getItems()) {
      String userRole = user.getRole().getRoleName();

      switch (userRole) {
        case "MENTOR" :
          userRepository.updateQuota(user.getUserSeq(), 7);
          break;
        case "MENTEE" :
          userRepository.updateQuota(user.getUserSeq(), 4);
          break;
        default: break;
      }
    }
  }
}
