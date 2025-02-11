package com.enf.controller;

import com.enf.model.dto.request.SignupDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<ResultResponse> signup(@Valid @RequestBody SignupDTO signupDTO) {

    ResultResponse resultResponse = authService.signup(signupDTO);

    return new ResponseEntity<>(resultResponse, resultResponse.getStatus());
  }
}
