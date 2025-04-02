package com.enf.domain.repository;

import com.enf.domain.entity.CategoryEntity;
import com.enf.domain.entity.RoleEntity;
import com.enf.domain.entity.UserEntity;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByProviderId(String providerId);

  Optional<UserEntity> findByProviderId(String providerId);

  // providerId를 기준으로 lastLoginAt 업데이트하는 메서드
  // check : 아직 querydsl 설정을 넣기 전이라 일단은 jpql을 사용하였음, 추후 변경 필요
  @Modifying
  @Transactional
  @Query("UPDATE user u SET u.lastLoginAt = CURRENT_TIMESTAMP WHERE u.userSeq = :userSeq")
  void updateLastLoginAtByUserSeq(@Param("userSeq") Long userSeq);
 
  boolean existsByNickname(String nickname);

  Optional<UserEntity> findByUserSeq(Long userSeq);

  @Modifying
  @Transactional
  @Query("UPDATE user u SET u.refreshToken = :refreshToken WHERE u.userSeq = :userSeq")
  void updateRefreshToken(@Param("userSeq")Long userSeq, @Param("refreshToken") String refreshToken);

  @Modifying
  @Transactional
  @Query("UPDATE user u SET u.nickname = :nickname WHERE u.userSeq = :userSeq")
  void updateNicknameByUserSeq(@Param("userSeq")Long userSeq, @Param("nickname")String nickname);

  @Modifying
  @Transactional
  @Query("UPDATE user u SET u.category = :category WHERE u.userSeq = :userSeq")
  void updateCategoryByUserSeq(@Param("userSeq")Long userSeq, @Param("category")CategoryEntity category);

  List<UserEntity> findAllByRole_RoleSeq(Long roleSeq);

  /**
   * 특정 역할을 가진 사용자 중 특정 기간에 가입한 사용자 목록 조회
   * @param roleSeq 역할 시퀀스 번호
   * @param startDateTime 조회 시작 일시
   * @param endDateTime 조회 종료 일시
   * @return 조건에 맞는 사용자 목록
   */
  List<UserEntity> findByRoleRoleSeqAndCreateAtBetween(Long roleSeq, LocalDateTime startDateTime, LocalDateTime endDateTime);

  @Modifying
  @Transactional
  @Query("UPDATE user u SET u.deleteAt = CURRENT_TIMESTAMP WHERE u.userSeq = :userSeq")
  void pendingWithdrawal(@Param("userSeq")Long userSeq);

  @Modifying
  @Transactional
  @Query("SELECT u FROM user u "
      + "WHERE u.deleteAt IS NOT NULL "
      + "AND DATEDIFF(CURRENT_DATE, u.deleteAt) >= 30 "
      + "AND u.role.roleName = 'WITHDRAWAL_PENDING'")
  List<UserEntity> getWithdrawalPendingUsers();

  @Modifying
  @Transactional
  @Query("UPDATE user u SET u.nickname = :nickname, u.role = :role WHERE u.userSeq = :userSeq")
  void withdrawal(@Param("userSeq")Long userSeq, @Param("role")RoleEntity role, @Param("nickname")String nickname);

  @Modifying
  @Transactional
  @Query("UPDATE user u SET u.deleteAt = NULL WHERE u.userSeq = :userSeq")
  void cancelWithdrawal(Long userSeq);

  // UT 테스트를 위한 메서드(삭제 예정)
  UserEntity findByNickname(String nickname);

  @Modifying
  @Transactional
  @Query("UPDATE user u "
      + "SET u.quota = CASE WHEN u.quota > 0 THEN u.quota - 1 ELSE u.quota END "
      + "WHERE u.userSeq = :userSeq")
  void reduceQuota(@Param("userSeq") Long userSeq);

  @Modifying
  @Transactional
  @Query("UPDATE user u SET u.quota = :quota WHERE u.userSeq = :userSeq")
  void updateQuota(@Param("userSeq") Long userSeq, @Param("quota") int quota);
}


