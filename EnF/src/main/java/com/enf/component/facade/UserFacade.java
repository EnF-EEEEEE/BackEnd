package com.enf.component.facade;

import com.enf.component.token.HttpCookieUtil;
import com.enf.component.token.TokenProvider;
import com.enf.entity.*;
import com.enf.exception.GlobalException;
import com.enf.model.dto.auth.AuthTokenDTO;
import com.enf.model.dto.request.user.UserCategoryDTO;
import com.enf.model.type.FailedResultType;
import com.enf.model.type.TokenType;
import com.enf.repository.*;
import com.enf.repository.querydsl.UserQueryRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
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

  /**
   * 회원 탈퇴 보류 처리
   *
   * @param user UserEntity
   */
  public void pendingWithdrawal(UserEntity user) {
    userRepository.pendingWithdrawal(user.getUserSeq());
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

  public void generateAndSetToken(UserEntity user, HttpServletResponse response) {
    AuthTokenDTO tokens = tokenProvider.generateAuthToken(user.getUserSeq(), user.getRole().getRoleName());
    ResponseCookie responseCookie = HttpCookieUtil.addCookieToResponse(tokens.getRefreshToken());
    userRepository.updateRefreshToken(user.getUserSeq(), tokens.getRefreshToken());
    response.addHeader(TokenType.ACCESS.getValue(), "Bearer " + tokens.getAccessToken());
    response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
  }

  public boolean validateToken(String token) {
    return tokenProvider.validateToken(token);
  }

  public UserEntity getUserByToken(String token) {
    Long userSeq = tokenProvider.getUserSeqFromToken(token);
    return findByUserSeq(userSeq);
  }

  // ============================= Quota 관련 메서드 =============================

  public void saveQuota(UserEntity user) {
    quotaRepository.save(
        QuotaEntity.builder()
            .user(user)
            .quota(user.getRole().getRoleName().equals("MENTEE") ? 4 : 7)
            .build()
    );
  }

  public void reduceQuota(UserEntity user) {
    quotaRepository.reduceQuota(user);
  }

  public List<QuotaEntity> getQuotas() {
    return quotaRepository.findAll();
  }

  public void resetQuota(UserEntity user, int quota) {
    quotaRepository.updateQuota(user, quota);
  }

  public List<UserEntity> getWithdrawalPendingUsers() {
    return userRepository.getWithdrawalPendingUsers();
  }

  public void withdrawal(UserEntity user) {
    RoleEntity role = roleRepository.findByRoleName("WITHDRAWAL");
    String nickname = "떠나간 새";

    userRepository.withdrawal(user.getUserSeq(), role, nickname);
  }

  public void cancelWithdrawal(UserEntity user) {
    userRepository.cancelWithdrawal(user.getUserSeq());
  }
}
