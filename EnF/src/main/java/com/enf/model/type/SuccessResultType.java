package com.enf.model.type;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessResultType {
  SUCCESS_SIGNUP(HttpStatus.OK, "회원가입 성공")
  ;

  private final HttpStatus status;
  private final String message;
}
