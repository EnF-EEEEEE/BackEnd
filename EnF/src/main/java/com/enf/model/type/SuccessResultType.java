package com.enf.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessResultType {
  SUCCESS_CHECK_NICKNAME(HttpStatus.OK, "닉네임 중복 체크 성공"),
  ;

  private final HttpStatus status;
  private final String message;
}
