package com.enf.domain.model.type;

import lombok.Getter;

@Getter
public enum UrlType {

  // 카카오 토큰 요청 URL
  KAKAO_TOKEN_URL("https://kauth.kakao.com/oauth/token"),

  // 카카오 사용자 정보 요청 URL
  KAKAO_USER_INFO_URL("https://kapi.kakao.com/v2/user/me"),

  // 카카오 사용자 탈퇴 요청 URL
  KAKAO_UNLINK_URL("https://kapi.kakao.com/v1/user/unlink"),

  // 프론트엔드 로컬 URL
  FRONT_LOCAL_URL("http://localhost:3000"),

  // 백엔드 로컬 URL
  BACK_LOCAL_URL("http://localhost:8080"),

  // 운영 서버 URL
  PROD_SERVER_URL("https://api.dearbirdy.xyz"),

  // 개발 서버 URL
  DEV_SERVER_URL("https://dev.dearbirdy.xyz"),

  // Dear Birdy URL
  DEAR_BIRDY_URL("https://www.dearbirdy.xyz"),
  ;

  private final String url;

  UrlType(String value) {
    this.url = value;
  }
}
