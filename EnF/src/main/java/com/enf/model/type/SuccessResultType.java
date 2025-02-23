package com.enf.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessResultType {
  SUCCESS_KAKAO_LOGIN(HttpStatus.OK, "로그인 성공"),
  SUCCESS_KAKAO_SIGNUP(HttpStatus.OK, "회원가입 성공"),
  SUCCESS_CHECK_NICKNAME(HttpStatus.OK, "닉네임 중복 체크 성공"),
  SUCCESS_ADDITIONAL_USER_INFO(HttpStatus.OK, "추가 정보 입력 성공"),
  SUCCESS_GET_USER_INFO(HttpStatus.OK, "회원 정보 조회 성공"),
  ;

  private final HttpStatus status;
  private final String message;
}
