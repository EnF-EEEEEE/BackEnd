package com.enf.service.impl;

import com.enf.component.KakaoAuthHandler;
import com.enf.component.UserAccessHandler;
import com.enf.component.token.HttpCookieUtil;
import com.enf.component.token.TokenProvider;
import com.enf.entity.RoleEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.auth.UserDetailsDTO;
import com.enf.model.dto.request.auth.KakaoUserDetailsDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.type.SuccessResultType;
import com.enf.repository.RoleRepository;
import com.enf.repository.UserRepository;
import com.enf.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
      RoleEntity userRole = roleRepository.findByRoleName("UNKNOWN")
              .orElseThrow(()->new RuntimeException("Role USER not found"));

      UserEntity saveUser = userRepository.save(KakaoUserDetailsDTO.of(kakaoUserDetails, userRole));

      // AccessToken발급
      String accessToken = tokenProvider.generateAccessToken(saveUser.getUserSeq(),saveUser.getRole().getRoleName());
      response.addHeader("access", "Bearer " + accessToken);

      // RefreshToken발급
      String refreshToken = tokenProvider.generateRefreshToken(saveUser.getUserSeq(),saveUser.getRole().getRoleName());
      ResponseCookie responseCookie = HttpCookieUtil.addCookieToResponse(refreshToken);

      // Cookie값으로 Header설정
      response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

      return ResultResponse.of(SuccessResultType.SUCCESS_KAKAO_SIGNUP);
    }

    // [로그인] : 로그인시 마지막 접속일자 갱신
    userRepository.updateLastLoginAtByProviderId(kakaoUserDetails.getProviderId());
    UserEntity user = userRepository.findByProviderId(kakaoUserDetails.getProviderId())
            .orElseThrow(()->new RuntimeException("User not found"));

    // AccessToken발급
    String accessToken = tokenProvider.generateAccessToken(user.getUserSeq(),user.getRole().getRoleName());
    response.addHeader("access", "Bearer " + accessToken);

    // RefreshToken발급
    String refreshToken = tokenProvider.generateRefreshToken(user.getUserSeq(),user.getRole().getRoleName());
    ResponseCookie responseCookie = HttpCookieUtil.addCookieToResponse(refreshToken);

    // Cookie값으로 Header설정
    response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

    return ResultResponse.of(SuccessResultType.SUCCESS_KAKAO_LOGIN);
  }

  @Override
  public UserDetails loadUserById(Long userSeq) throws UsernameNotFoundException {
    UserEntity user = userAccessHandler.findByUserSeq(userSeq);
    return new UserDetailsDTO(user);
  }

}
