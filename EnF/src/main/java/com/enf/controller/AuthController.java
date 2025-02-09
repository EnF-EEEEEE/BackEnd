package com.enf.controller;

import com.enf.model.dto.response.ResultResponse;
import com.enf.service.AuthService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  public final AuthService authService;


  /**
   * 카카오 OAuth 회원가입 OR 로그인
   *
   * @param code 카카오 인가 코드 DTO
   */
  @GetMapping("/kakao")
  public ResponseEntity<ResultResponse> oAuthForKakao(@RequestParam("code") String code) {

    ResultResponse resultResponse = authService.oAuthForKakao(code);

    log.info("code {} ", code);
    return new ResponseEntity<>(resultResponse, resultResponse.getStatus());
  }

}
