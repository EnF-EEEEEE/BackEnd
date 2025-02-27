package com.enf.controller;

import com.enf.model.dto.response.ResultResponse;
import com.enf.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


//Test URL : https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=962b24a09f42380d01cc640c02a3b71d&redirect_uri=http://localhost:8080/api/v1/auth/callback
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  /**
   * 카카오 OAuth 회원가입 또는 로그인 API
   *
   * @param code     카카오 인가 코드
   * @param response HTTP 응답 객체
   * @return 회원가입 또는 로그인 결과
   */
  @GetMapping("/kakao")
  public ResponseEntity<ResultResponse> oAuthForKakao(
      HttpServletResponse response,
      @RequestParam("code") String code) {

    ResultResponse resultResponse = authService.oAuthForKakao(response, code);
    return new ResponseEntity<>(resultResponse, resultResponse.getStatus());
  }

  /**
   * 카카오 OAuth 콜백 URL (인가 코드 반환)
   *
   * @param code 카카오 인가 코드
   * @return 인가 코드 문자열 반환
   */
  @GetMapping("/callback")
  public String redirectForSNSLogin(@RequestParam("code") String code) {
    log.info("code {} ", code);
    return code;
  }

  /**
   * Access Token 재발급 API
   *
   * @param request  HTTP 요청 객체
   * @param response HTTP 응답 객체
   * @return 새롭게 발급된 Token
   */
  @GetMapping("/reissue-token")
  public ResponseEntity<ResultResponse> reissueToken(
      HttpServletRequest request,
      HttpServletResponse response) {

    ResultResponse resultResponse = authService.reissueToken(request, response);
    return new ResponseEntity<>(resultResponse, resultResponse.getStatus());
  }
}