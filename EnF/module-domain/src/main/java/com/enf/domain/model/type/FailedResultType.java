package com.enf.domain.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FailedResultType {

  ACCESS_TOKEN_RETRIEVAL(HttpStatus.UNAUTHORIZED, "액세스 토큰을 가져오는 데 실패했습니다."),
  USER_INFO_RETRIEVAL(HttpStatus.UNAUTHORIZED, "사용자 정보를 가져오는 데 실패했습니다."),
  USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않은 회원입니다."),
  ROLE_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않은 권한입니다."),
  COOKIE_IS_NULL(HttpStatus.FORBIDDEN,"쿠키값이 존재하지 않습니다."),
  REFRESH_TOKEN_IS_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh 토큰이 만료되었습니다!"),
  BAD_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "발급된 Refresh 토큰이 아닙니다."),
  LETTER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않은 편지 일련번호 입니다."),
  MENTEE_PERMISSION_DENIED(HttpStatus.BAD_REQUEST, "멘티는 접근 권한이 없습니다."),
  MENTOR_PERMISSION_DENIED(HttpStatus.BAD_REQUEST, "멘토는 접근 권한이 없습니다."),
  ALREADY_REPLIED(HttpStatus.BAD_REQUEST, "이미 답장한 편지는 넘길 수 없습니다."),
  UNLINK_FAILED(HttpStatus.BAD_REQUEST, "회원탈퇴 실패 했습니다"),
  ALREADY_PENALTY(HttpStatus.BAD_REQUEST, "이미 영구 정지된 회원입니다."),
  QUOTA_IS_EMPTY(HttpStatus.BAD_REQUEST, "남은 편지 개수가 0개 입니다."),
  ;

  private final HttpStatus status;
  private final String message;
}
