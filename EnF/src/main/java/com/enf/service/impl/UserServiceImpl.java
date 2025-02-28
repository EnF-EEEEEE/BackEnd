package com.enf.service.impl;

import com.enf.component.facade.UserFacade;
import com.enf.entity.BirdEntity;
import com.enf.entity.CategoryEntity;
import com.enf.entity.RoleEntity;
import com.enf.entity.UserEntity;
import com.enf.model.dto.request.user.AdditionalInfoDTO;
import com.enf.model.dto.request.user.UpdateNicknameDTO;
import com.enf.model.dto.request.user.UserCategoryDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.dto.response.user.UserInfoDTO;
import com.enf.model.type.SuccessResultType;
import com.enf.model.type.TokenType;
import com.enf.service.UserService;
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
  public ResultResponse additionalInfo(HttpServletRequest request, HttpServletResponse response, AdditionalInfoDTO additionalInfoDTO) {
    UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
    BirdEntity bird = userFacade.findBirdByBirdName(additionalInfoDTO.getBirdName());
    RoleEntity role = userFacade.findRoleByRoleName(additionalInfoDTO.getUserRole());
    CategoryEntity category = userFacade.saveCategory(role, additionalInfoDTO.getUserCategory());
    UserEntity saveUser = AdditionalInfoDTO.of(user, bird, role, category, additionalInfoDTO);

    userFacade.saveUser(saveUser);
    userFacade.saveQuota(saveUser);
    userFacade.generateAndSetToken(user, response);

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
    return new ResultResponse(SuccessResultType.SUCCESS_GET_USER_INFO, UserInfoDTO.of(user));
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