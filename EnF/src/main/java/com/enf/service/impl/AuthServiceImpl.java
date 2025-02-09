package com.enf.service.impl;

import com.enf.Entity.UserEntity;
import com.enf.component.KakaoAuthHandler;
import com.enf.model.dto.request.auth.KakaoUserDetailsDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.type.SuccessResultType;
import com.enf.repository.UserRepository;
import com.enf.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  public final UserRepository userRepository;
  public final KakaoAuthHandler kakaoAuthHandler;


  @Override
  public ResultResponse oAuthForKakao(String code) {
    String accessToken = kakaoAuthHandler.getAccessToken(code);
    UserEntity user = KakaoUserDetailsDTO.of(kakaoAuthHandler.getUserDetails(accessToken));

    if (!userRepository.existsByProviderId(user.getProviderId())) {
      userRepository.save(user);

      return ResultResponse.of(SuccessResultType.SUCCESS_KAKAO_SIGNUP);
    }

    return ResultResponse.of(SuccessResultType.SUCCESS_KAKAO_LOGIN);
  }
}
