package com.enf.service.impl;

import com.enf.component.KakaoAuthHandler;
import com.enf.component.facade.UserFacade;
import com.enf.entity.RoleEntity;
import com.enf.entity.UserEntity;
import com.enf.exception.GlobalException;
import com.enf.model.dto.auth.UserDetailsDTO;
import com.enf.model.dto.request.auth.KakaoUserDetailsDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.dto.response.user.UserInfoDTO;
import com.enf.model.type.FailedResultType;
import com.enf.model.type.SuccessResultType;
import com.enf.model.type.TokenType;
import com.enf.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final KakaoAuthHandler kakaoAuthHandler;
  private final UserFacade userFacade;

  /**
   * 카카오 OAuth 로그인 또는 회원가입 처리
   *
   * @param response HTTP 응답 객체
   * @param code     카카오 OAuth 인증 코드
   * @return 로그인 또는 회원가입 결과 응답 객체
   */
  @Override
  public ResultResponse oAuthForKakao(HttpServletRequest request, HttpServletResponse response, String code) {
    // 카카오 액세스 토큰 요청
    String kakaoAccessToken = kakaoAuthHandler.getAccessToken(request,code);
    // 카카오 사용자 정보 요청
    KakaoUserDetailsDTO kakaoUserDetails = kakaoAuthHandler.getUserDetails(kakaoAccessToken);

    return userFacade.findByProviderId(kakaoUserDetails.getProviderId())
        .map(user -> {
          if (user.getDeleteAt() != null) {
            userFacade.cancelWithdrawal(user);
          }
          log.info("Kakao 로그인 진행");
          userFacade.updateLastLoginAt(user.getUserSeq());
          userFacade.generateAndSetToken(user, response);
          if (user.getRole().getRoleName().equals("UNKNOWN")) {
            return ResultResponse.of(SuccessResultType.SUCCESS_KAKAO_LOGIN);
          }
          UserInfoDTO userInfo = userFacade.getUserInfo(user);
          return new ResultResponse(SuccessResultType.SUCCESS_KAKAO_LOGIN, userInfo);
        })
        .orElseGet(() -> {
          log.info("Kakao 회원가입 진행");
          RoleEntity userRole = userFacade.findRoleByRoleName("UNKNOWN");
          UserEntity saveUser = userFacade.saveUser(KakaoUserDetailsDTO.of(kakaoUserDetails, userRole));
          userFacade.generateAndSetToken(saveUser, response);
          return ResultResponse.of(SuccessResultType.SUCCESS_KAKAO_SIGNUP);
        });
  }

  /**
   * 사용자 ID를 기반으로 사용자 정보를 로드
   *
   * @param userSeq 사용자 일련번호
   * @return 사용자 정보 객체 (UserDetails)
   * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 예외 발생
   */
  @Override
  public UserDetails loadUserById(Long userSeq) throws UsernameNotFoundException {
    UserEntity user = userFacade.findByUserSeq(userSeq);
    return new UserDetailsDTO(user);
  }

  /**
   * Refresh Token을 검증하고 새로운 액세스 토큰을 발급
   *
   * @param request  HTTP 요청 객체 (쿠키에서 Refresh Token 추출)
   * @param response HTTP 응답 객체 (새로운 토큰 설정)
   * @return 토큰 재발급 결과 응답 객체
   */
  @Override
  public ResultResponse reissueToken(HttpServletRequest request, HttpServletResponse response) {
    // 쿠키에서 Refresh Token 추출
    String refreshToken = extractTokenFromCookies(request.getCookies());
    log.info("Refresh token: {}", refreshToken);

    // Refresh Token이 없거나 유효하지 않은 경우 예외 발생
    if (refreshToken == null || !userFacade.validateToken(refreshToken)) {
      log.warn("유효하지 않거나 만료된 Refresh Token");
      throw new GlobalException(FailedResultType.REFRESH_TOKEN_IS_EXPIRED);
    }

    // 토큰에서 사용자 정보 추출
    UserEntity user = userFacade.getUserByToken(refreshToken);

    // 서버에 저장된 Refresh Token과 비교하여 불일치 시 예외 발생
    if (!refreshToken.equals(user.getRefreshToken())) {
      throw new GlobalException(FailedResultType.BAD_REFRESH_TOKEN);
    }

    // 새로운 토큰 발급 및 설정
    userFacade.generateAndSetToken(user, response);
    return ResultResponse.of(SuccessResultType.SUCCESS_REISSUE_TOKEN);
  }

  /**
   * 회원 탈퇴 처리하는 메서드
   *
   * @param request  HTTP 요청 객체
   * @return 회원탈퇴 결과 응답 객체
   */
  @Override
  public ResultResponse withdrawal(HttpServletRequest request) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    userFacade.pendingWithdrawal(user);

    return ResultResponse.of(SuccessResultType.SUCCESS_KAKAO_WITHDRAWAL);
  }

  /**
   * 쿠키에서 Refresh Token을 추출하는 메서드
   *
   * @param cookies HTTP 요청에서 전달된 쿠키 배열
   * @return Refresh Token 값 (없을 경우 null 반환)
   * @throws GlobalException 쿠키가 존재하지 않는 경우 예외 발생
   */
  private String extractTokenFromCookies(Cookie[] cookies) {
    if (cookies == null || cookies.length == 0) {
      log.warn("쿠키가 존재하지 않습니다.");
      throw new GlobalException(FailedResultType.COOKIE_IS_NULL);
    }

    return Arrays.stream(cookies)
        .filter(cookie -> TokenType.REFRESH.getValue().equals(cookie.getName()))
        .map(Cookie::getValue)
        .findFirst()
        .orElse(null);
  }
}