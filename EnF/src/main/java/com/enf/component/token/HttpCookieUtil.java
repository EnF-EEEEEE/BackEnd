package com.enf.component.token;

import org.springframework.http.ResponseCookie;

public class HttpCookieUtil {
    public static ResponseCookie addCookieToResponse(String refreshToken){
        return ResponseCookie.from("refresh", refreshToken)
                .httpOnly(true)               //JavaScript에서 접근 불가능
                .secure(true)                 //HTTP에서만 전송
                .path("/")                    //전체 도메인에서 유효
                .maxAge(7200)   //2시간 유효
                .build();
    }
}
