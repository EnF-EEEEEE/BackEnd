package com.enf.service.impl;

import com.enf.component.KakaoAuthHandler;
import com.enf.component.UserAccessHandler;
import com.enf.component.token.HttpCookieUtil;
import com.enf.component.token.TokenProvider;
import com.enf.entity.RoleEntity;
import com.enf.entity.UserEntity;
import com.enf.exception.GlobalException;
import com.enf.model.dto.auth.AuthTokenDTO;
import com.enf.model.dto.auth.UserDetailsDTO;
import com.enf.model.dto.request.auth.KakaoUserDetailsDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.type.FailedResultType;
import com.enf.model.type.SuccessResultType;
import com.enf.model.type.TokenType;
import com.enf.repository.RoleRepository;
import com.enf.repository.UserRepository;
import com.enf.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  public final UserRepository userRepository;
  public final KakaoAuthHandler kakaoAuthHandler;
  public final TokenProvider tokenProvider;
  private final RoleRepository roleRepository;
  private final UserAccessHandler userAccessHandler;

  // Kakao Talk 회원가입 및 로그인
  @Override
  public ResultResponse oAuthForKakao(HttpServletResponse response, String code) {
    String kakaoAccessToken = kakaoAuthHandler.getAccessToken(code);
    KakaoUserDetailsDTO kakaoUserDetails = kakaoAuthHandler.getUserDetails(kakaoAccessToken);

    // [회원가입] : 회원의 providerId가 없는경우
    if (!userRepository.existsByProviderId(kakaoUserDetails.getProviderId())) {
      log.info("kakao로 회원가입 진행");
      RoleEntity userRole = roleRepository.findByRoleName("UNKNOWN")
              .orElseThrow(()->new RuntimeException("Role USER not found"));

      UserEntity saveUser = userRepository.save(KakaoUserDetailsDTO.of(kakaoUserDetails, userRole));

      // Token발급 Access & Refresh
      AuthTokenDTO tokens = tokenProvider.generateAuthToken(saveUser.getUserSeq(), saveUser.getRole().getRoleName());

      response.addHeader("access", "Bearer " + tokens.getAccessToken());  // 헤더 값에 access token설정
      ResponseCookie responseCookie = HttpCookieUtil.addCookieToResponse(tokens.getRefreshToken());

      // 생성한 토큰 값 저장
      userRepository.updateRefreshToken(saveUser.getUserSeq(), tokens.getRefreshToken());

      // Cookie값으로 Header설정
      response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

      return ResultResponse.of(SuccessResultType.SUCCESS_KAKAO_SIGNUP);
    }

    log.info("kakao로 로그인 진행");
    // [로그인] : 로그인시 마지막 접속일자 갱신
    userRepository.updateLastLoginAtByProviderId(kakaoUserDetails.getProviderId());
    UserEntity user = userRepository.findByProviderId(kakaoUserDetails.getProviderId())
            .orElseThrow(()->new RuntimeException("User not found"));


    // Token발급 Access & Refresh
    AuthTokenDTO tokens = tokenProvider.generateAuthToken(user.getUserSeq(), user.getRole().getRoleName());

    // 헤더 값에 accessToken설정
    response.addHeader("access", "Bearer " + tokens.getAccessToken());

    // 쿠키에 refreshToken 설정
    ResponseCookie responseCookie = HttpCookieUtil.addCookieToResponse(tokens.getRefreshToken());

    // 생성한 토큰 값 저장
    userRepository.updateRefreshToken(user.getUserSeq(), tokens.getRefreshToken());

    // Cookie값으로 Header설정
    response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

    return ResultResponse.of(SuccessResultType.SUCCESS_KAKAO_LOGIN);
  }

  @Override
  public UserDetails loadUserById(Long userSeq) throws UsernameNotFoundException {
    UserEntity user = userAccessHandler.findByUserSeq(userSeq);
    return new UserDetailsDTO(user);
  }

  @Override
  public ResultResponse reissueToken(HttpServletRequest request, HttpServletResponse response) {

    //쿠키 확인
    String refreshToken = extractTokenFromCookies(request.getCookies());
    log.info("Refresh token: {}", refreshToken);

    if (refreshToken == null || !tokenProvider.validateToken(refreshToken)) {
      log.warn("유효하지 않거나 만료된 Refresh Token");
      throw new GlobalException(FailedResultType.REFRESH_TOKEN_IS_EXPIRED);
    }

    Long userSeq = tokenProvider.getUserSeqFromToken(refreshToken);
    UserEntity user = userAccessHandler.findByUserSeq(userSeq);

    //서버에 저장된 refreshToken정보와 다를경우
    if (!refreshToken.equals(user.getRefreshToken())) {
      throw new GlobalException(FailedResultType.BAD_REFRESH_TOKEN);
    }

    // Token발급 Access & Refresh
    AuthTokenDTO tokens = tokenProvider.generateAuthToken(user.getUserSeq(), user.getRole().getRoleName());

    // 헤더 값에 accessToken설정
    response.addHeader("access", "Bearer " + tokens.getAccessToken());

    // 쿠키에 refreshToken 설정
    ResponseCookie responseCookie = HttpCookieUtil.addCookieToResponse(tokens.getRefreshToken());

    // 생성한 토큰 값 저장
    userRepository.updateRefreshToken(user.getUserSeq(), tokens.getRefreshToken());

    // Cookie값으로 Header설정
    response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

    return ResultResponse.of(SuccessResultType.SUCCESS_REISSUE_TOKEN);
  }

  /**
   * 쿠키에서 토큰 추출
   *
   * @param cookies 쿠키 배열
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
