package com.enf.email.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Value("${api.key.name}")
    private String apiKeyHeaderName;

    @Value("${api.key.value}")
    private String apiKeyValue;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String requestApiKey = request.getHeader(apiKeyHeaderName);

        // API 키 검증
        if (requestApiKey == null || !requestApiKey.equals(apiKeyValue)) {
            log.warn("유효하지 않은 API 키: {}", requestApiKey);

             // 또는 아래와 같이 직접 응답을 작성할 수도 있습니다
             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
             response.getWriter().write("인증되지 않은 요청입니다");
             return false;
        }

        return true;
    }
}
