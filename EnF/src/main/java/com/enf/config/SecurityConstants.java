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
        "/api/v1/user/info",
        "/api/v1/user/update/nickname",
        "/api/v1/letter/list/save",
        "/api/v1/letter/list/pending",
        "/api/v1/letter/list/all",
        "/api/v1/letter/details",
        "/api/v1/notification/subscribe",
    };

    public static final String[] menteeUrls = {
        "/api/v1/letter/send",
        "/api/v1/letter/thanks",
        "/api/v1/letter/save",
    };

    public static final String[] mentorUrls = {
        "/api/v1/user/update/category",
        "/api/v1/letter/reply",
        "/api/v1/letter/throw",
    };

    // 허용 Urls
    public static String[] allowedUrls = Stream.concat(Arrays.stream(swaggerUrls), Arrays.stream(allowUrls))
            .toArray(String[]::new);

}
