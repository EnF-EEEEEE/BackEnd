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
import com.enf.model.dto.request.auth.KakaoUserDetailsDTO;
import com.enf.model.dto.request.letter.SendLetterDTO;
import com.enf.model.dto.request.user.AdditionalInfoDTO;
import com.enf.model.dto.request.user.UserCategoryDTO;
import com.enf.model.dto.response.letter.LetterHistoryDTO;
import com.enf.model.dto.response.user.UserProfileDTO;
import com.enf.model.type.FailedResultType;
import com.enf.model.type.TokenType;
import com.enf.repository.BirdRepository;
import com.enf.repository.CategoryRepository;
import com.enf.repository.LetterStatusRepository;
import com.enf.repository.QuotaRepository;
import com.enf.repository.RoleRepository;
import com.enf.repository.UserRepository;
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
  private final LetterStatusRepository letterStatusRepository;

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
  public UserEntity findByProviderId(KakaoUserDetailsDTO kakaoUserDetails) {
    return userRepository.findByProviderId(kakaoUserDetails.getProviderId())
        .orElseGet(() -> {
          RoleEntity userRole = findRoleByRoleName("UNKNOWN");
          return saveUser(KakaoUserDetailsDTO.of(kakaoUserDetails, userRole));
        });
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
   * 추가 정보 저장
   *
   * @param user 기존 사용자 정보
   * @param additionalInfoDTO 추가 정보 DTO
   * @return 저장된 UserEntity
   */
  public UserEntity saveAdditionalInfo(UserEntity user, AdditionalInfoDTO additionalInfoDTO) {
    BirdEntity bird = findBirdByBirdName(additionalInfoDTO.getBirdName());
    RoleEntity role = findRoleByRoleName(additionalInfoDTO.getUserRole());
    CategoryEntity category = saveCategory(role, additionalInfoDTO.getUserCategory());

    UserEntity saveUser = AdditionalInfoDTO.of(user, bird, role, category, additionalInfoDTO);
    saveUser(saveUser);
    saveQuota(saveUser);

    return saveUser;
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
   * 전체 사용자 수 조회
   */
  public long getTotalUserCount() {
    return userRepository.count();
  }

  /**
   * 새 이름, 카테고리 정보와 일치하는 UserEntity 조회
   *
   * @param sendLetter 작성한 사용자의 편지 정보
   */
  public UserEntity getMentorByBirdAndCategory(SendLetterDTO sendLetter) {
    String birdName = sendLetter.getBirdName();
    String categoryName = sendLetter.getCategoryName();

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
    Long letterStatusSeq = letterStatus.getLetterStatusSeq();

    return userQueryRepository.getMentor(birdName, categoryName, letterStatusSeq);
  }

  /**
   * 회원 탈퇴 보류 처리
   *
   * @param user UserEntity
   */
  public void pendingWithdrawal(UserEntity user) {
    userRepository.pendingWithdrawal(user.getUserSeq());
  }


  /**
   * 회원 탈퇴 보류 중인 사용자 목록 조회
   *
   * @return 탈퇴 보류 중인 사용자 리스트
   */
  public List<UserEntity> getWithdrawalPendingUsers() {
    return userRepository.getWithdrawalPendingUsers();
  }

  /**
   * 회원 탈퇴 처리
   *
   * @param user 탈퇴할 사용자 정보
   */
  public void withdrawal(UserEntity user) {
    RoleEntity role = roleRepository.findByRoleName("WITHDRAWAL");
    String nickname = "떠나간 새";

    userRepository.withdrawal(user.getUserSeq(), role, nickname);
  }

  /**
   * **회원 탈퇴 취소**
   *
   * @param user 탈퇴 취소할 사용자 정보
   */
  public void cancelWithdrawal(UserEntity user) {
    userRepository.cancelWithdrawal(user.getUserSeq());
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
   * 사용자에게 Access Token과 Refresh Token을 발급하고, 응답 헤더와 쿠키에 추가
   *
   * @param user 사용자 정보
   * @param response HTTP 응답 객체
   */
  public void generateAndSetToken(UserEntity user, HttpServletResponse response) {
    AuthTokenDTO tokens = tokenProvider.generateAuthToken(user.getUserSeq(), user.getRole().getRoleName());
    ResponseCookie responseCookie = HttpCookieUtil.addCookieToResponse(tokens.getRefreshToken());
    userRepository.updateRefreshToken(user.getUserSeq(), tokens.getRefreshToken());
    response.addHeader(TokenType.ACCESS.getValue(), "Bearer " + tokens.getAccessToken());
    response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
  }

  /**
   * 토큰 유효성 검사
   *
   * @param token 검증할 토큰
   * @return 유효 여부 (true: 유효, false: 만료 또는 잘못된 토큰)
   */
  public boolean validateToken(String token) {
    return tokenProvider.validateToken(token);
  }


  /**
   * 토큰을 이용하여 사용자 조회
   *
   * @param token 조회할 토큰
   * @return 조회된 사용자 정보
   */
  public UserEntity getUserByToken(String token) {
    Long userSeq = tokenProvider.getUserSeqFromToken(token);
    return findByUserSeq(userSeq);
  }

  // ============================= Quota 관련 메서드 =============================

  /**
   * 사용자 할당량 저장
   *
   * @param user 사용자 정보
   */
  public void saveQuota(UserEntity user) {
    quotaRepository.save(
        QuotaEntity.builder()
            .user(user)
            .quota(user.getRole().getRoleName().equals("MENTEE") ? 4 : 7)
            .build()
    );
  }

  /**
   * 사용자의 할당량 감소
   *
   * @param user 사용자 정보
   */
  public void reduceQuota(UserEntity user) {
    quotaRepository.reduceQuota(user);
  }

  /**
   * 전체 사용자 할당량 조회
   *
   * @return 모든 사용자 할당량 리스트
   */
  public List<QuotaEntity> getQuotas() {
    return quotaRepository.findAll();
  }

  public int getQuotaByUserSeq(UserEntity user) {
    return quotaRepository.findByUser(user).getQuota();
  }


  /**
   * 사용자의 할당량을 지정된 값으로 초기화
   *
   * @param user 사용자 정보
   * @param quota 초기화할 할당량 값
   */
  public void resetQuota(UserEntity user, int quota) {
    quotaRepository.updateQuota(user, quota);
  }

  public UserProfileDTO getUserInfo(UserEntity user, LetterHistoryDTO letterHistory) {
    QuotaEntity quota = quotaRepository.findByUser(user);

    boolean isRead = switch (user.getRole().getRoleName()) {
      case "MENTEE" -> letterStatusRepository
          .existsMenteeRead(user.getUserSeq());
      case "MENTOR" -> letterStatusRepository
          .existsMentorRead(user.getUserSeq());
      default -> false;
    };

    return UserProfileDTO.of(user, quota.getQuota(), isRead, letterHistory);
  }
}
