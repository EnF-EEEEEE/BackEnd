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

    };

    public static final String[] adminUrls = {

    };

    public static final String[] userUrls = {
            "/api/v1/user/check-nickname",
            "/api/v1/user/additional-info",
            "/api/v1/letter/send",
    };

    // 허용 Urls
    public static String[] allowedUrls = Stream.concat(Arrays.stream(swaggerUrls), Arrays.stream(allowUrls))
            .toArray(String[]::new);

}
