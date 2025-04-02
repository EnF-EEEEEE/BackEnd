package com.enf.api.service.impl;

import com.enf.api.component.facade.LetterFacade;
import com.enf.api.component.facade.UserFacade;
import com.enf.domain.entity.UserEntity;
import com.enf.domain.model.dto.request.user.AdditionalInfoDTO;
import com.enf.domain.model.dto.request.user.UpdateNicknameDTO;
import com.enf.domain.model.dto.request.user.UserCategoryDTO;
import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.domain.model.dto.response.letter.LetterHistoryDTO;
import com.enf.domain.model.dto.response.user.UserProfileDTO;
import com.enf.domain.model.type.SuccessResultType;
import com.enf.domain.model.type.TokenType;
import com.enf.api.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserFacade userFacade;
  private final LetterFacade letterFacade;

  /**
   * 닉네임 중복 확인
   *
   * @param nickname 클라이언트가 입력한 닉네임
   * @return 중복 여부를 포함한 응답 객체
   */
  @Override
  public ResultResponse checkNickname(String nickname) {
    boolean isDuplicate = userFacade.existsByNickname(nickname);
    log.info("{} : {}", nickname, isDuplicate ? "중복된 닉네임" : "사용가능한 닉네임");
    return new ResultResponse(SuccessResultType.SUCCESS_CHECK_NICKNAME, isDuplicate);
  }

  /**
   * 추가 사용자 정보 입력
   *
   * @param request           HTTP 요청 객체
   * @param response          HTTP 응답 객체
   * @param additionalInfoDTO 사용자 추가 정보 DTO
   * @return 추가 정보 저장 결과 응답 객체
   */
  @Override
  public ResultResponse additionalInfo(HttpServletRequest request,
      HttpServletResponse response, AdditionalInfoDTO additionalInfoDTO) {

    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    UserEntity saveUser = userFacade.saveAdditionalInfo(user, additionalInfoDTO);

    userFacade.generateAndSetToken(saveUser, response);
    return ResultResponse.of(SuccessResultType.SUCCESS_ADDITIONAL_USER_INFO);
  }

  /**
   * 사용자 정보 조회
   *
   * @param request HTTP 요청 객체
   * @return 사용자 정보 DTO를 포함한 응답 객체
   */
  @Override
  public ResultResponse userInfo(HttpServletRequest request) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    LetterHistoryDTO letterHistory = letterFacade.getLetterHistory(user);
    UserProfileDTO userProfile = userFacade.getUserInfo(user, letterHistory);
    return new ResultResponse(SuccessResultType.SUCCESS_GET_USER_INFO, userProfile);
  }

  /**
   * 닉네임 수정
   *
   * @param request  HTTP 요청 객체
   * @param nickname 변경할 닉네임 DTO
   * @return 닉네임 변경 결과 응답 객체
   */
  @Override
  public ResultResponse updateNickname(HttpServletRequest request, UpdateNicknameDTO nickname) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    userFacade.updateNicknameByUserSeq(user.getUserSeq(), nickname.getNickname());
    return ResultResponse.of(SuccessResultType.SUCCESS_UPDATE_NICKNAME);
  }

  /**
   * 사용자 카테고리 수정
   *
   * @param request      HTTP 요청 객체
   * @param userCategory 변경할 카테고리 정보 DTO
   * @return 카테고리 변경 결과 응답 객체
   */
  @Override
  public ResultResponse updateCategory(HttpServletRequest request, UserCategoryDTO userCategory) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

    userFacade.updateCategory(user.getUserSeq(), UserCategoryDTO.of(userCategory));
    return ResultResponse.of(SuccessResultType.SUCCESS_UPDATE_CATEGORY);
  }
}