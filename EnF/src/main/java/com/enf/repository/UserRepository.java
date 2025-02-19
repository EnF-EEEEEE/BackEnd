package com.enf.repository;

import com.enf.Entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  UserEntity findByEmail(String email);

  boolean existsByProviderId(String providerId);

  // providerId를 기준으로 lastLoginAt 업데이트하는 메서드
  // TODO : 아직 querydsl 설정을 넣기 전이라 일단은 jpql을 사용하였음, 추후 변경 필요
  @Modifying
  @Transactional
  @Query("UPDATE Users u SET u.lastLoginAt = CURRENT_TIMESTAMP WHERE u.providerId = :providerId")
  void updateLastLoginAtByProviderId(String providerId);
}
