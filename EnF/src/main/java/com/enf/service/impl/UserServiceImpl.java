package com.enf.service.impl;

import com.enf.entity.BirdEntity;
import com.enf.entity.CategoryEntity;
import com.enf.entity.RoleEntity;
import com.enf.entity.UserEntity;
import com.enf.exception.GlobalException;
import com.enf.model.dto.request.user.AdditionalInfoDTO;
import com.enf.model.dto.request.user.UserCategoryDTO;
import com.enf.model.dto.response.ResultResponse;
import com.enf.model.type.FailedResultType;
import com.enf.model.type.SuccessResultType;
import com.enf.repository.BirdRepository;
import com.enf.repository.CategoryRepository;
import com.enf.repository.RoleRepository;
import com.enf.repository.UserRepository;
import com.enf.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final BirdRepository birdRepository;
  private final RoleRepository roleRepository;
  private final CategoryRepository categoryRepository;

  /**
   * 회원가입 or 회원정보 수정시 닉네임 중복 검증을 위한 메서드
   *
   * @param nickname 클라이언트 측으로 부터 전달 받은 nickname 값
   *
   * @return {@link ResultResponse}
   * - 중복인 닉네임인 경우 ture
   * - 사용가능 닉네임인 경우 false
   */
  @Override
  public ResultResponse checkNickname(String nickname) {

    if (userRepository.existsByNickname(nickname)) {
      log.info("{} : 중복된 닉네임", nickname);
      return new ResultResponse(SuccessResultType.SUCCESS_CHECK_NICKNAME, true);
    }

    log.info("{} : 사용가능한 닉네임", nickname);
    return new ResultResponse(SuccessResultType.SUCCESS_CHECK_NICKNAME, false);
  }

  @Override
  public ResultResponse additionalInfo(HttpServletRequest request,
      AdditionalInfoDTO additionalInfoDTO) {

    // 사용자 정보 추출, 추후 Jwt 토큰 로직 구현 후 수정할 예정
    UserEntity user = userRepository.findById(1L)
        .orElseThrow(() -> new GlobalException(FailedResultType.USER_NOT_FOUND));

    BirdEntity bird = birdRepository.findByBirdName(additionalInfoDTO.getBirdName());

    RoleEntity role = roleRepository.findByRoleName(additionalInfoDTO.getAgeGroup());

    CategoryEntity category = role.getRoleName().equals("senior")
        ? categoryRepository.save(UserCategoryDTO.of(additionalInfoDTO.getUserCategory()))
        : null;

    userRepository.save(AdditionalInfoDTO.of(user, bird, role, category, additionalInfoDTO));

    return ResultResponse.of(SuccessResultType.SUCCESS_ADDITIONAL_USER_INFO);
  }
}
