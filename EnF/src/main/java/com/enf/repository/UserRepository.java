package com.enf.repository;

import com.enf.entity.CategoryEntity;
import com.enf.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByProviderId(String providerId);

  Optional<UserEntity> findByProviderId(String providerId);

  // providerId를 기준으로 lastLoginAt 업데이트하는 메서드
  // check : 아직 querydsl 설정을 넣기 전이라 일단은 jpql을 사용하였음, 추후 변경 필요
  @Modifying
  @Transactional
  @Query("UPDATE user u SET u.lastLoginAt = CURRENT_TIMESTAMP WHERE u.userSeq = :userSeq")
  void updateLastLoginAtByUserSeq(Long userSeq);
 
  boolean existsByNickname(String nickname);

  Optional<UserEntity> findByUserSeq(Long userSeq);

  @Modifying
  @Transactional
  @Query("UPDATE user u SET u.refreshToken = :refreshToken WHERE u.userSeq = :userSeq")
  void updateRefreshToken(@Param("userSeq") Long userSeq, @Param("refreshToken") String refreshToken);

  @Modifying
  @Transactional
  @Query("UPDATE user u set u.nickname = :nickname WHERE u.userSeq = :userSeq")
  void updateNicknameByUserSeq(Long userSeq, String nickname);

  @Modifying
  @Transactional
  @Query("UPDATE user u set u.category = :category WHERE u.userSeq = :userSeq")
  void updateCategoryByUserSeq(Long userSeq, CategoryEntity category);

  List<UserEntity> findAllByRole_RoleSeq(Long roleSeq);

  /**
   * 특정 역할을 가진 사용자 중 특정 기간에 가입한 사용자 목록 조회
   * @param roleSeq 역할 시퀀스 번호
   * @param startDateTime 조회 시작 일시
   * @param endDateTime 조회 종료 일시
   * @return 조건에 맞는 사용자 목록
   */
  List<UserEntity> findByRoleRoleSeqAndCreateAtBetween(Long roleSeq, LocalDateTime startDateTime, LocalDateTime endDateTime);
}


