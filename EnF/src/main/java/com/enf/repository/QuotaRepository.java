package com.enf.repository;

import com.enf.entity.QuotaEntity;
import com.enf.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotaRepository extends JpaRepository<QuotaEntity, Long> {

  @Modifying
  @Transactional
  @Query("UPDATE quota q SET quota = :quota WHERE q.user = :user")
  void updateQuota(@Param("user") UserEntity user, @Param("quota") int quota);

  @Modifying
  @Transactional
  @Query("UPDATE quota q "
      + "SET q.quota = CASE WHEN q.quota > 0 THEN q.quota - 1 ELSE q.quota END "
      + "WHERE q.quotaSeq = :quotaSeq")
  void reduceQuota(@Param("quotaSeq") Long quotaSeq);

  QuotaEntity findByUser(UserEntity user);
}