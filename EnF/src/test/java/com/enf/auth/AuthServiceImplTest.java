package com.enf.auth;

import com.enf.component.KakaoAuthHandler;
import com.enf.component.token.TokenProvider;
import com.enf.entity.RoleEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.request.auth.KakaoUserDetailsDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.type.SuccessResultType;
import com.enf.repository.RoleRepository;
import com.enf.repository.UserRepository;
import com.enf.service.impl.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KakaoAuthHandler kakaoAuthHandler;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;


    @InjectMocks    //실제 서비스 불러옴 -> 실제 서비스를 실행한다.
    private AuthServiceImpl authServiceImpl;

    private KakaoUserDetailsDTO kakaoUserDetails;
    private UserEntity saveUser;

    private static final Long USERSEQ = 1L;
    private static final String EMAIL = "test@email.com";
    private static final String NICKNAME = "nickname";
    private static final String CODE = "kakaoCode";
    private static final String KAKAO_TOKEN = "kakao-token";
    private static final String KAKAO_PROVIDER = "kakao";
    private static final String KAKAO_PROVIDER_ID = "kakao_provider_id";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String USER_ROLE = "USER";
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp(){
        Map<String, Object> kakaoAccount = new HashMap<>();
        kakaoAccount.put("email", EMAIL);

        Map<String, Object> properties = new HashMap<>();
        properties.put("nickname", NICKNAME);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", KAKAO_PROVIDER_ID);
        attributes.put("kakao_account", kakaoAccount);
        attributes.put("properties", properties);

        kakaoUserDetails = new KakaoUserDetailsDTO(attributes);

        roleEntity = RoleEntity.builder()
                .roleSeq(1L)
                .roleName(USER_ROLE)
                .build();

        saveUser = UserEntity.builder()
                .userSeq(USERSEQ)
                .email(EMAIL)
                .nickname(NICKNAME)
                .provider(KAKAO_PROVIDER)
                .providerId(KAKAO_PROVIDER_ID)
                .createAt(LocalDateTime.now())
                .lastLoginAt(LocalDateTime.now())
                .role(roleEntity)
                .build();

    }

    @Test
    @DisplayName("Kakao 회원가입 성공")
    public void testOAuthForKakao_signup() {
        // given
        // when 사전세팅 -> thenReturn 로직을 실제 실행하는것이아닌 정상 동작여부를 확인한다!?
//        when(kakaoAuthHandler.getAccessToken(CODE)).thenReturn(KAKAO_TOKEN);
        when(kakaoAuthHandler.getUserDetails(KAKAO_TOKEN)).thenReturn(kakaoUserDetails);
        when(userRepository.existsByProviderId(KAKAO_PROVIDER_ID)).thenReturn(false);
        when(roleRepository.findByRoleName(USER_ROLE)).thenReturn(roleEntity);

        when(userRepository.save(any(UserEntity.class))).thenReturn(saveUser);

        when(tokenProvider.generateAccessToken(USERSEQ, USER_ROLE)).thenReturn(ACCESS_TOKEN);

        // when
        ResultResponse resultResponse = authServiceImpl.oAuthForKakao(request,response, CODE);

        // then
        assertEquals(SuccessResultType.SUCCESS_KAKAO_SIGNUP.getStatus(), resultResponse.getStatus());

    }

    @Test
    @DisplayName("Kakao 로그인 성공")
    public void testOAuthForKakao_login() {
        // given
//        when(kakaoAuthHandler.getAccessToken(CODE)).thenReturn(KAKAO_TOKEN);
        when(kakaoAuthHandler.getUserDetails(KAKAO_TOKEN)).thenReturn(kakaoUserDetails);
        when(userRepository.existsByProviderId(KAKAO_PROVIDER_ID)).thenReturn(true);
        when(userRepository.findByProviderId(KAKAO_PROVIDER_ID)).thenReturn(Optional.of(saveUser));

        when(tokenProvider.generateAccessToken(USERSEQ, USER_ROLE)).thenReturn(ACCESS_TOKEN);
        when(tokenProvider.generateRefreshToken(USERSEQ, USER_ROLE)).thenReturn(REFRESH_TOKEN);

        // when
        ResultResponse resultResponse = authServiceImpl.oAuthForKakao(request, response, CODE);

        // then
        assertEquals(SuccessResultType.SUCCESS_KAKAO_LOGIN.getStatus(), resultResponse.getStatus());
    }

}
