package com.enf.component;



import com.enf.exception.GlobalException;
import com.enf.model.dto.request.auth.KakaoUserDetailsDTO;
import com.enf.model.type.FailedResultType;
import com.enf.model.type.UrlType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoAuthHandler {

  @Value("${DEV.AUTH.OAUTH.REGISTRATION.KAKAO.client-id}")
  private String clientId;

  @Value("${DEV.AUTH.OAUTH.REGISTRATION.KAKAO.client-secret}")
  private String clientSecretId;

  @Value("${DEV.AUTH.OAUTH.REGISTRATION.KAKAO.redirect-uri}")
  private String redirectUri;

  @Value("${DEV.AUTH.OAUTH.REGISTRATION.KAKAO.admin-key}")
  private String adminKey;

  public String getAccessToken(String code) {
    log.info("KakaoAuthHandler.getAccessToken code={}", code);
    HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = getMultiValueMapHttpEntity(code);

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        UrlType.KAKAO_TOKEN_URL.getUrl(),
        HttpMethod.POST,
        kakaoTokenRequest,
        new ParameterizedTypeReference<>() {}
    );

    // access-token 반환
    if (response.getStatusCode() == HttpStatus.OK) {
      Map<String, Object> responseBody = response.getBody();
      if (responseBody != null && responseBody.containsKey("access_token")) {
        log.info("getAccessToken success");
        return responseBody.get("access_token").toString();
      }
    }

    log.info("KakaoAuthHandler.getAccessToken failed");
    throw new GlobalException(FailedResultType.ACCESS_TOKEN_RETRIEVAL);
  }

  private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(String code) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    // HTTP Body 생성
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("client_id", clientId);
    body.add("client_secret", clientSecretId);
    body.add("redirect_uri", redirectUri);
    body.add("code", code);

    // HTTP 요청 보내기
    return new HttpEntity<>(body, headers);
  }

  // 사용자 정보 가지고 오기
  public KakaoUserDetailsDTO getUserDetails(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    // HTTP 요청 보내기
    HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
    RestTemplate rt = new RestTemplate();
    ResponseEntity<Map<String, Object>> response = rt.exchange(
        UrlType.KAKAO_USER_INFO_URL.getUrl(),
        HttpMethod.POST,
        kakaoUserInfoRequest,
        new ParameterizedTypeReference<>() {}
    );

    if (response.getStatusCode() == HttpStatus.OK) {
      if (response.getBody() != null) {
        log.info("getUserDetails success");
        return new KakaoUserDetailsDTO(response.getBody());
      }
    }

    log.info("KakaoAuthHandler.getUserDetails failed");
    throw new GlobalException(FailedResultType.USER_INFO_RETRIEVAL);
  }

}
