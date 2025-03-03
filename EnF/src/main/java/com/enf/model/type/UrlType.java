package com.enf.model.type;

import lombok.Getter;

@Getter
public enum UrlType {

  KAKAO_TOKEN_URL("https://kauth.kakao.com/oauth/token"),
  KAKAO_USER_INFO_URL("https://kapi.kakao.com/v2/user/me"),
  ;

  private final String url;

  UrlType(String value) {
    this.url = value;
  }
}
