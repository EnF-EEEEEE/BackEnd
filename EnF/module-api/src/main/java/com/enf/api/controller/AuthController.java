package com.enf.api.controller;

import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


//Local Test URL : https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=3d37d53cb427928f2a93ea16263d08de&redirect_uri=http://localhost:8080/api/v1/auth/callback
//DevServer Test URL : https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=3d37d53cb427928f2a93ea16263d08de&redirect_uri=https://api.dearbirdy.xyz/api/v1/auth/callback
//DevServer Test URL : https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=3d37d53cb427928f2a93ea16263d08de&redirect_uri=http://localhost:3000/callback
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
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam("code") String code) {

    ResultResponse resultResponse = authService.oAuthForKakao(request,response, code);
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

  @GetMapping("/withdrawal")
  public ResponseEntity<ResultResponse> withdrawal(HttpServletRequest request) {

    ResultResponse resultResponse = authService.withdrawal(request);
    return new ResponseEntity<>(resultResponse, resultResponse.getStatus());
  }
}