package com.enf.model.type;

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
  ;

  private final HttpStatus status;
  private final String message;
}
