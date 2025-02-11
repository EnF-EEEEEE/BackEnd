package com.enf.service.impl;

import com.enf.entity.UserEntity;
import com.enf.model.dto.request.SignupDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.type.AgeType;
import com.enf.model.type.SuccessResultType;
import com.enf.repository.UserRepository;
import com.enf.service.AuthService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;

  /**
   * 회원가입 기능
   *
   * SignupDTO : email, password, nickname, tel, birthday
   *
   * (임시)
   * AgeType : SENIOR, YOUTH
   * SENIOR : birthday가 올해를 기준으로 40 초과일 경우
   * YOUTH : birthday가 올해를 기준으로 40 미만일 경우
   */
  @Override
  public ResultResponse signup(SignupDTO signupDTO) {

    userRepository.save(UserEntity.builder()
        .email(signupDTO.getEmail())
        .password(signupDTO.getPassword())
        .nickname(signupDTO.getNickname())
        .tel(signupDTO.getTel())
        .ageType(LocalDate.parse(signupDTO.getBirthday()).isBefore(LocalDate.now().minusYears(40))
            ? AgeType.SENIOR
            : AgeType.YOUTH)
        .build());

    return ResultResponse.of(SuccessResultType.SUCCESS_SIGNUP);
  }
}
