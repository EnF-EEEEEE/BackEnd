package com.enf.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessResultType {
  SUCCESS_KAKAO_LOGIN(HttpStatus.OK, "로그인 성공"),
  SUCCESS_KAKAO_SIGNUP(HttpStatus.CREATED, "회원가입 성공"),
  SUCCESS_CHECK_NICKNAME(HttpStatus.OK, "닉네임 중복 체크 성공"),
  SUCCESS_ADDITIONAL_USER_INFO(HttpStatus.OK, "추가 정보 입력 성공"),
  SUCCESS_REISSUE_TOKEN(HttpStatus.OK, "토큰 재발급 성공"),
  SUCCESS_GET_USER_INFO(HttpStatus.OK, "회원 정보 조회 성공"),
  SUCCESS_UPDATE_NICKNAME(HttpStatus.OK, "닉네임 수정 성공"),
  SUCCESS_UPDATE_CATEGORY(HttpStatus.OK, "카테고리 수정 성공"),
  SUCCESS_SEND_LETTER(HttpStatus.OK, "편지 전송 성공"),
  SUCCESS_RECEIVE_LETTER(HttpStatus.OK, "편지 답장 성공"),
  SUCCESS_GET_ALL_LETTER(HttpStatus.OK, "모든 편지 조회 성공"),
  SUCCESS_GET_PENDING_LETTER(HttpStatus.OK, "담장을 기다리는 편지 조회 성공"),
  SUCCESS_GET_SAVE_LETTER(HttpStatus.OK, "저장한 편지 조회 성공"),
  SUCCESS_SAVE_LETTER(HttpStatus.OK, "편지 저장 성공"),
  SUCCESS_GET_LETTER_DETAILS(HttpStatus.OK, "편지 상세 조회 성공"),
  SUCCESS_THROW_LETTER(HttpStatus.OK, "편지 넘기기 성공"),
  SUCCESS_THANKS_TO_MENTOR(HttpStatus.OK, "고마움 표시하기 성공"),
  SUCCESS_GET_THROW_LETTER_CATEGORY(HttpStatus.OK, "카테고리별 넘긴 편지 개수 조회 성공"),
  SUCCESS_KAKAO_WITHDRAWAL(HttpStatus.OK, "회원탈퇴 성공"),
  SUCCESS_GET_TEST_RESULT_BIRDY(HttpStatus.OK, "버디 테스트 새 유형 조회 성공"),
  SUCCESS_GET_LETTER_BIRDY(HttpStatus.OK, "카테고리 새 유형 조회 성공"),
  SUCCESS_GET_ALL_BIRDY(HttpStatus.OK, "모든 새 유형 조회 성공"),
  SUCCESS_GET_BIRDY_TIPS(HttpStatus.OK, "버디 팁 조회 성공"),
  ;

  private final HttpStatus status;
  private final String message;
}
