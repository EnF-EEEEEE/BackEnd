package com.enf.config;

import com.enf.model.type.TokenType;
import com.enf.model.type.UrlType;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  private static final String ALLOW_METHOD_NAMES = "GET,HEAD,POST,DELETE,TRACE,OPTIONS,PATCH,PUT";

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    registry.addMapping("/**") // CORS 설정을 모든 URL에 적용

        .allowedOriginPatterns(
            UrlType.FRONT_LOCAL_URL.getUrl(),
            UrlType.DEV_SERVER_URL.getUrl(),
            UrlType.BACK_LOCAL_URL.getUrl()
        )
        .allowedMethods(ALLOW_METHOD_NAMES.split(","))  // 허용할 HTTP Method 목록
        .allowedHeaders("*")        // 모든 HTTP header 허용
        .allowCredentials(true)     // 자격 증명 허용
        .exposedHeaders(
            TokenType.ACCESS.getValue(),
            HttpHeaders.LOCATION,
            HttpHeaders.CONTENT_TYPE);   // 클라이언트에 노출할 헤더 목록
  }

}
