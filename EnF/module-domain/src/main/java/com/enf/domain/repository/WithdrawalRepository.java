package com.enf.domain.repository;

import com.enf.domain.entity.WithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface WithdrawalRepository extends JpaRepository<WithdrawalEntity, Long> {

  @Transactional
  void deleteByWithdrawalUser(String nickname);

}
