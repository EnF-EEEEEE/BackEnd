package com.enf.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.enf.model.dto.response.ResultResponse;
import com.enf.model.type.SuccessResultType;
import com.enf.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  // mock 객체
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceImpl userService;

  // 상수 데이터
  private final String NICKNAME = "nickname";


  // test
  @Test
  @DisplayName("닉네임 중복 체크 성공 : 중복된 닉네임")
  void checkNickname_duplicate() {
    // given
    when(userRepository.existsByNickname(NICKNAME)).thenReturn(true);

    // when
    ResultResponse response = userService.checkNickname(NICKNAME);

    // then
    assertEquals(SuccessResultType.SUCCESS_CHECK_NICKNAME.getStatus(), response.getStatus());
    assertEquals(true, response.getData());
  }

  @Test
  @DisplayName("닉네임 중복 체크 성공 : 사용가능한 닉네임")
  void checkNickname_available() {
    // given
    when(userRepository.existsByNickname(NICKNAME)).thenReturn(false);

    // when
    ResultResponse response = userService.checkNickname(NICKNAME);

    // then
    assertEquals(SuccessResultType.SUCCESS_CHECK_NICKNAME.getStatus(), response.getStatus());
    assertEquals(false, response.getData());
  }

}