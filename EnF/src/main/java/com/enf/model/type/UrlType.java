package com.enf.model.type;

import lombok.Getter;

@Getter
public enum UrlType {

  KAKAO_TOKEN_URL("https://kauth.kakao.com/oauth/token"),
  KAKAO_USER_INFO_URL("https://kapi.kakao.com/v2/user/me"),
  KAKAO_UNLINK_URL("https://kapi.kakao.com/v1/user/unlink"),
  FRONT_LOCAL_URL("http://localhost:3000"),
  PROD_SERVER_URL("https://api.dearbirdy.xyz"),
  DEV_SERVER_URL("https://dev.dearbirdy.xyz"),
  BACK_LOCAL_URL("http://localhost:8080"),
  ;

  private final String url;

  UrlType(String value) {
    this.url = value;
  }
}
