package com.enf.controller;

import com.enf.model.dto.response.ResultResponse;
import com.enf.service.AuthService;
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

  public final AuthService authService;

  /**
   * 카카오 OAuth 회원가입 OR 로그인
   * @param code 카카오 인가 코드 DTO
   */
  @GetMapping("/kakao")
  public ResponseEntity<ResultResponse> oAuthForKakao(@RequestParam("code") String code) {
    ResultResponse resultResponse = authService.oAuthForKakao(code);
    return new ResponseEntity<>(resultResponse, resultResponse.getStatus());
  }

  /**
   * 카카오 OAuth 회원가입 OR 로그인 redirect url
   * @param code 카카오 인가 코드 DTO
   */
  @GetMapping("/callback")
  public String redirectForSNSLogin(@RequestParam("code") String code) {
    log.info("code {} ", code);
    return code;
  }
}
