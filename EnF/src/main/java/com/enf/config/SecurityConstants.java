package com.enf.config;

import java.util.Arrays;
import java.util.stream.Stream;

public class SecurityConstants {
    //swager 경로
    public static final String[] swaggerUrls = {"/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**"};
    //인증 없이 허용할 경로 리스트
    public static final String[] allowUrls = {
            "/api/v1/auth/callback",    // kakao sns login redirect url
            "/api/v1/auth/kakao",
            "/api/v1/auth/reissue-token",
            "/api/v1/admin/login",
            "/api/v1/admin/callback",
            "/api/v1/admin/login",
            "/admin/login",
            "/favicon.ico",

    };

    public static final String[] adminUrls = {
            "/api/v1/admin/dashboard",
            "/api/v1/admin/stats",
            "/api/v1/admin/dashboard-data",
            "/api/v1/admin/inquiries",
            "/api/v1/admin/inquiries/**",
            "/admin/dashboard",
            "/admin/inquiries",
            "/admin/inquiries/**",
            "/api/v1/admin/letters",
            "/api/v1/admin/letters/**",
            "/api/v1/admin/reports",
            "/api/v1/admin/reports/**",

    };

    public static final String[] userUrls = {
            "/api/v1/user/check-nickname",
            "/api/v1/user/additional-info",
            "/api/v1/letter/send",
            "/api/v1/reports"
    };

    // 허용 Urls
    public static String[] allowedUrls = Stream.concat(Arrays.stream(swaggerUrls), Arrays.stream(allowUrls))
            .toArray(String[]::new);

}
