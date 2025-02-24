package com.enf.repository;

import com.enf.entity.CategoryEntity;
import com.enf.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByProviderId(String providerId);

  // providerId를 기준으로 lastLoginAt 업데이트하는 메서드
  // check : 아직 querydsl 설정을 넣기 전이라 일단은 jpql을 사용하였음, 추후 변경 필요
  @Modifying
  @Transactional
  @Query("UPDATE user u SET u.lastLoginAt = CURRENT_TIMESTAMP WHERE u.providerId = :providerId")
  void updateLastLoginAtByProviderId(String providerId);
 
  boolean existsByNickname(String nickname);

  @Modifying
  @Transactional
  @Query("UPDATE user u set u.nickname = :nickname WHERE u.userSeq = :userSeq")
  void updateNicknameByUserSeq(Long userSeq, String nickname);

  @Modifying
  @Transactional
  @Query("UPDATE user u set u.category = :category WHERE u.userSeq = :userSeq")
  void updateCategoryByUserSeq(Long userSeq, CategoryEntity category);

}
