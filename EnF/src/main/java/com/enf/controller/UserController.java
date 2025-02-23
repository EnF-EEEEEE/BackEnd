package com.enf.controller;

import com.enf.model.dto.request.user.AdditionalInfoDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

  private final UserService userService;

  @GetMapping("/check-nickname")
  public ResponseEntity<ResultResponse> checkNickname(@RequestParam("nickname") String nickname) {

    ResultResponse response = userService.checkNickname(nickname);

    return new ResponseEntity<>(response, response.getStatus());
  }

  @PostMapping("/additional-info")
  public ResponseEntity<ResultResponse> additionalInfo(
      HttpServletRequest request, @RequestBody AdditionalInfoDTO additionalInfoDTO) {

    ResultResponse response = userService.additionalInfo(request, additionalInfoDTO);

    return new ResponseEntity<>(response, response.getStatus());
  }

  @GetMapping("/info")
  public ResponseEntity<ResultResponse> userInfo(HttpServletRequest request) {

    ResultResponse response = userService.userInfo(request);

    return new ResponseEntity<>(response, response.getStatus());
  }

}
