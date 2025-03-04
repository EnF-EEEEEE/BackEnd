package com.enf.model.type;

import lombok.Getter;

@Getter
public enum UrlType {

  KAKAO_TOKEN_URL("https://kauth.kakao.com/oauth/token"),
  KAKAO_USER_INFO_URL("https://kapi.kakao.com/v2/user/me"),
  FRONT_URL("추가 예정"),
  ;

  private final String url;

  UrlType(String value) {
    this.url = value;
  }
}
