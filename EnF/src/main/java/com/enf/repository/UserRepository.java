package com.enf.repository;

import com.enf.entity.CategoryEntity;
import com.enf.entity.RoleEntity;
import com.enf.entity.UserEntity;
import jakarta.transaction.Transactional;
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
}
