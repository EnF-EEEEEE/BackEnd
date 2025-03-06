package com.enf.config;

import com.enf.component.token.JwtUtil;
import com.enf.component.token.TokenProvider;
import com.enf.model.type.ConstantsType;
import com.enf.model.type.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // options 확인
        if(HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("request URI : {}", request.getRequestURI());
        // 검증 필요한 경로인지 확인
        if(isExcludedPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Header에서 Access 추출 & Bearer 시작 여부 및 null 값 검증
        String token = resolveToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Access-Token 만료시간 검증
        if (!tokenProvider.validateToken(token)) {
            log.info("access-token is expired");
            setResponse(response);
            return;
        }

        setAuthentication(token);
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token) {
        Long userSeq = tokenProvider.getUserSeqFromToken(token);
        Authentication authentication = jwtUtil.getAuthentication(userSeq);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Access-Token 만료시 401 반환
    private void setResponse(HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // CORS 헤더 추가
        response.setHeader("Access-Control-Allow-Origin", ConstantsType.WEB_URL);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET,HEAD,POST,DELETE,TRACE,OPTIONS,PATCH,PUT");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Expose-Headers", "access-token, Location");

        Map<String, String> body = Map.of("message", "access-token 만료");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(TokenType.ACCESS.getValue());
        log.info("bearerToken: {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }else {
            log.info("Invalid JWT token");
            return null;
        }
    }

    private boolean isExcludedPath(String requestURI) {
        return requestURI.equals("/")
            || requestURI.equals("/api/v1/auth/callback")
            || requestURI.equals("/api/v1/auth/kakao")
            || requestURI.equals("/api/v1/auth/reissue-token")
            || requestURI.matches("/v3/api-docs/.*")
            || requestURI.matches("/v3/api-docs")
            || requestURI.matches("/swagger-ui/.*")
            || requestURI.equals("/swagger-ui.html")
            || requestURI.matches("/swagger-resources/.*");
    }
}
