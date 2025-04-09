package com.enf.api.controller;

import com.enf.api.component.badword.annotation.BadWordCheck;
import com.enf.domain.model.dto.request.user.AdditionalInfoDTO;
import com.enf.domain.model.dto.request.user.UpdateNicknameDTO;
import com.enf.domain.model.dto.request.user.UserCategoryDTO;
import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

  private final UserService userService;

  /**
   * 닉네임 중복 확인 API
   *
   * @param nickname 클라이언트에서 전달한 닉네임
   * @return 닉네임 중복 여부 결과
   */
  @GetMapping("/check-nickname")
  public ResponseEntity<ResultResponse> checkNickname(
    @RequestParam("nickname") 
    @BadWordCheck
    String nickname) {

    ResultResponse response = userService.checkNickname(nickname);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * 사용자 추가 정보 입력 API
   *
   * @param request           HTTP 요청 객체
   * @param response          HTTP 응답 객체
   * @param additionalInfoDTO 사용자 추가 정보 DTO
   * @return 추가 정보 입력 결과
   */
  @PostMapping("/additional-info")
  public ResponseEntity<ResultResponse> additionalInfo(
      HttpServletRequest request, HttpServletResponse response,
      @RequestBody
      @BadWordCheck
      AdditionalInfoDTO additionalInfoDTO) {

    ResultResponse resultResponse = userService.additionalInfo(request, response,
        additionalInfoDTO);
    return new ResponseEntity<>(resultResponse, resultResponse.getStatus());
  }

  /**
   * 사용자 정보 조회 API
   *
   * @param request HTTP 요청 객체
   * @return 사용자 정보 조회 결과
   */
  @GetMapping("/info")
  public ResponseEntity<ResultResponse> userInfo(HttpServletRequest request) {

    ResultResponse response = userService.userInfo(request);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * 사용자 닉네임 변경 API
   *
   * @param request  HTTP 요청 객체
   * @param nickname 변경할 닉네임 정보 DTO
   * @return 닉네임 변경 결과
   */
  @PostMapping("/update/nickname")
  public ResponseEntity<ResultResponse> updateNickname(
      HttpServletRequest request,
      @RequestBody
      @BadWordCheck
      UpdateNicknameDTO nickname) {

    ResultResponse response = userService.updateNickname(request, nickname);
    return new ResponseEntity<>(response, response.getStatus());
  }

  /**
   * 사용자 카테고리 변경 API
   *
   * @param request      HTTP 요청 객체
   * @param userCategory 변경할 카테고리 정보 DTO
   * @return 카테고리 변경 결과
   */
  @PostMapping("/update/category")
  public ResponseEntity<ResultResponse> updateCategory(
      HttpServletRequest request,
      @RequestBody UserCategoryDTO userCategory) {

    ResultResponse response = userService.updateCategory(request, userCategory);
    return new ResponseEntity<>(response, response.getStatus());
  }

  @PostMapping("/update/bird")
  public ResponseEntity<ResultResponse> updateBird(HttpServletRequest request, String birdName) {

    ResultResponse response = userService.updateBirdType(request, birdName);
    return new ResponseEntity<>(response, response.getStatus());
  }
}