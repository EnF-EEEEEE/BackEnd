package com.enf.service.impl;

import com.enf.model.dto.response.ResultResponse;
import com.enf.model.type.SuccessResultType;
import com.enf.repository.UserRepository;
import com.enf.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  /**
   * 회원가입 or 회원정보 수정시 닉네임 중복 검증을 위한 메서드
   *
   * @param nickname 클라이언트 측으로 부터 전달 받은 nickname 값
   *
   * @return {@link ResultResponse}
   * - 중복인 닉네임인 경우 ture
   * - 사용가능 닉네임인 경우 false
   */
  @Override
  public ResultResponse checkNickname(String nickname) {

    if (userRepository.existsByNickname(nickname)) {
      log.info("{} : 중복된 닉네임", nickname);
      return new ResultResponse(SuccessResultType.SUCCESS_CHECK_NICKNAME, true);
    }

    log.info("{} : 사용가능한 닉네임", nickname);
    return new ResultResponse(SuccessResultType.SUCCESS_CHECK_NICKNAME, false);
  }
}
