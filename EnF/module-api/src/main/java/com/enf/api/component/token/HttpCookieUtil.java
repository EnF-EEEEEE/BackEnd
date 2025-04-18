package com.enf.api.component.token;

import com.enf.domain.model.type.TokenType;
import org.springframework.http.ResponseCookie;

public class HttpCookieUtil {
    public static ResponseCookie addCookieToResponse(String refreshToken){
        return ResponseCookie.from(TokenType.REFRESH.getValue(), refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(2 * 60 * 60)
            .sameSite("None")
            .build();
    }
}
