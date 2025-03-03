package com.enf.component.facade;

import com.enf.component.token.HttpCookieUtil;
import com.enf.component.token.TokenProvider;
import com.enf.entity.BirdEntity;
import com.enf.entity.CategoryEntity;
import com.enf.entity.LetterStatusEntity;
import com.enf.entity.QuotaEntity;
import com.enf.entity.RoleEntity;
import com.enf.entity.UserEntity;
import com.enf.exception.GlobalException;
import com.enf.model.dto.auth.AuthTokenDTO;
import com.enf.model.dto.request.user.UserCategoryDTO;
import com.enf.model.type.FailedResultType;
import com.enf.model.type.TokenType;
import com.enf.repository.BirdRepository;
import com.enf.repository.CategoryRepository;
import com.enf.repository.QuotaRepository;
import com.enf.repository.RoleRepository;
import com.enf.repository.UserRepository;
import com.enf.repository.querydsl.UserQueryRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFacade {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final BirdRepository birdRepository;
  private final CategoryRepository categoryRepository;
  private final TokenProvider tokenProvider;
  private final QuotaRepository quotaRepository;
  private final UserQueryRepository userQueryRepository;

  // ============================= User 관련 메서드 =============================

  /**
   * userSeq 값과 일치하는 UserEntity 조회
   *
   * @param userSeq 사용자 일련번호
   * @return 조회된 UserEntity
   * @throws GlobalException 사용자를 찾을 수 없는 경우 예외 발생
   */
  public UserEntity findByUserSeq(Long userSeq) {
    log.info("findByUserSeq -> userSeq : {}", userSeq);
    return userRepository.findByUserSeq(userSeq)
        .orElseThrow(() -> new GlobalException(FailedResultType.USER_NOT_FOUND));
  }

  /**
   * User 정보 저장
   *
   * @param user 저장할 UserEntity 객체
   * @return 저장된 UserEntity
   */
  public UserEntity saveUser(UserEntity user) {
    return userRepository.save(user);
  }

  /**
   * providerId 값과 일치하는 UserEntity 조회
   *
   * @param providerId 외부 OAuth 제공자의 ID
   * @return Optional<UserEntity>
   */
  public Optional<UserEntity> findByProviderId(String providerId) {
    return userRepository.findByProviderId(providerId);
  }

  /**
   * userSeq 값과 일치하는 사용자의 마지막 로그인 시간 갱신
   *
   * @param userSeq 사용자 일련번호
   */
  public void updateLastLoginAt(Long userSeq) {
    userRepository.updateLastLoginAtByUserSeq(userSeq);
  }

  /**
   * 사용자의 닉네임이 존재하는지 확인
   *
   * @param nickname 확인할 닉네임
   * @return 중복 여부 (true: 존재함, false: 사용 가능)
   */
  public boolean existsByNickname(String nickname) {
    return userRepository.existsByNickname(nickname);
  }

  /**
   * 사용자의 닉네임을 변경
   *
   * @param userSeq 사용자 일련번호
   * @param nickname 변경할 닉네임
   */
  public void updateNicknameByUserSeq(Long userSeq, String nickname) {
    userRepository.updateNicknameByUserSeq(userSeq, nickname);
  }

  /**
   * 사용자의 카테고리를 변경
   *
   * @param userSeq 사용자 일련번호
   * @param category 변경할 카테고리
   */
  public void updateCategory(Long userSeq, CategoryEntity category) {
    userRepository.updateCategoryByUserSeq(userSeq, category);
  }

  /**
   * 새 이름, 카테고리 정보와 일치하는 UserEntity 조회
   *
   * @param birdName 작성한 사용자의 새이름
   * @param categoryName 작성한 편지의 카테고리
   */
  public UserEntity getMentorByBirdAndCategory(String birdName, String categoryName) {

    return userQueryRepository.getMentor(birdName, categoryName, null);
  }

  /**
   * 새로운 멘토 조회 (편지를 넘긴 사용자 제외)
   *
   * @param letterStatus 현재 편지의 상태 정보
   * @return 새로운 멘토 사용자 엔티티
   */
  public UserEntity getNewMentor(LetterStatusEntity letterStatus) {
    String birdName = letterStatus.getMenteeLetter().getBirdName();
    String categoryName = letterStatus.getMenteeLetter().getCategoryName();

    log.info("birdName : {}", birdName);
    log.info("categoryName : {}", categoryName);

    return userQueryRepository.getMentor(birdName, categoryName, letterStatus.getLetterStatusSeq());
  }


  // ============================= Role 관련 메서드 =============================

  /**
   * roleName과 일치하는 RoleEntity 조회
   *
   * @param roleName 역할 이름
   * @return 조회된 RoleEntity
   */
  public RoleEntity findRoleByRoleName(String roleName) {
    return roleRepository.findByRoleName(roleName);
  }

  // ============================= Bird 관련 메서드 =============================

  /**
   * birdName과 일치하는 BirdEntity 조회
   *
   * @param birdName 새 이름
   * @return 조회된 BirdEntity
   */
  public BirdEntity findBirdByBirdName(String birdName) {
    return birdRepository.findByBirdName(birdName);
  }

  // ============================= Category 관련 메서드 =============================

  /**
   * 카테고리 저장 (멘토 역할인 경우에만 저장)
   *
   * @param role 사용자 역할
   * @param additionalInfoDTO 추가 정보 DTO
   * @return 저장된 CategoryEntity (멘토가 아닌 경우 null)
   */
  public CategoryEntity saveCategory(RoleEntity role, UserCategoryDTO additionalInfoDTO) {
    if ("MENTOR".equals(role.getRoleName())) {
      return categoryRepository.save(UserCategoryDTO.of(additionalInfoDTO));
    }
    return null;
  }

  // ============================= Token 관련 메서드 =============================

  /**
   * Response 헤더에 AccessToken과 RefreshToken 추가
   *
   * @param user UserEntity
   * @param response HTTP 응답 객체
   */
  public void generateAndSetToken(UserEntity user, HttpServletResponse response) {
    AuthTokenDTO tokens = tokenProvider.generateAuthToken(user.getUserSeq(), user.getRole().getRoleName());

    // 쿠키에 RefreshToken 설정
    ResponseCookie responseCookie = HttpCookieUtil.addCookieToResponse(tokens.getRefreshToken());
    // RefreshToken을 DB에 저장
    userRepository.updateRefreshToken(user.getUserSeq(), tokens.getRefreshToken());

    // 헤더에 AccessToken & Cookie 추가
    response.addHeader(TokenType.ACCESS.getValue(), "Bearer " + tokens.getAccessToken());
    response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
  }

  /**
   * 토큰 유효성 검증
   *
   * @param token 검증할 토큰
   * @return 유효성 검증 결과 (true: 유효함, false: 유효하지 않음)
   */
  public boolean validateToken(String token) {
    return tokenProvider.validateToken(token);
  }

  /**
   * 토큰값으로 userSeq 값 추출 후 UserEntity 반환
   *
   * @param token 검증할 토큰
   * @return 조회된 UserEntity
   */
  public UserEntity getUserByToken(String token) {
    Long userSeq = tokenProvider.getUserSeqFromToken(token);
    return findByUserSeq(userSeq);
  }


  // ============================= Quota 관련 메서드 =============================

  /**
   * 사용자 역할별 편지 개수 할당
   *
   * @param user UserEntity
   */
  public void saveQuota(UserEntity user) {
    quotaRepository.save(
        QuotaEntity.builder()
            .user(user)
            .quota(user.getRole().getRoleName().equals("MENTEE") ? 4 : 7)
            .build()
    );
  }
}