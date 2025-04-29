package com.enf.batch.reader;

import com.enf.domain.entity.UserEntity;
import com.enf.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalUserReader {

  private final UserRepository userRepository;

  public List<UserEntity> getWithdrawalUsers() {
    return userRepository.getWithdrawalPendingUsers();
  }

}