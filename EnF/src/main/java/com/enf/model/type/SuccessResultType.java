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
  SUCCESS_REISSUE_TOKEN(HttpStatus.OK, "토큰 재발급 성공"),
  SUCCESS_GET_USER_INFO(HttpStatus.OK, "회원 정보 조회 성공"),
  SUCCESS_UPDATE_NICKNAME(HttpStatus.OK, "닉네임 수정 성공"),
  SUCCESS_UPDATE_CATEGORY(HttpStatus.OK, "카테고리 수정 성공"),
  SUCCESS_SEND_LETTER(HttpStatus.OK, "편지 전송 성공"),
  SUCCESS_RECEIVE_LETTER(HttpStatus.OK, "편지 답장 성공")
  ;

  private final HttpStatus status;
  private final String message;
}
