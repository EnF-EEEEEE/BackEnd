package com.enf.email.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Value("${api.key.name}")
    private String apiKeyHeaderName;

    @Value("${api.key.value}")
    private String apiKeyValue;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String apiKey = request.getHeader(apiKeyHeaderName);

        // API 키가 없거나 일치하지 않으면 403 응답
        if (apiKey == null || !apiKey.equals(apiKeyValue)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Invalid or missing API key");
            return false;
        }

        return true;
    }
}
