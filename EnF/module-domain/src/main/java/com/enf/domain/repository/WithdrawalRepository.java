package com.enf.domain.repository;

import com.enf.domain.entity.WithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalRepository extends JpaRepository<WithdrawalEntity, Long> {

  void deleteByWithdrawalUser(String user);

}
