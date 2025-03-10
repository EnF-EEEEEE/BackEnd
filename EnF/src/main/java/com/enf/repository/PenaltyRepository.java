package com.enf.repository;

import com.enf.entity.PenaltyEntity;
import com.enf.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PenaltyRepository extends JpaRepository<PenaltyEntity, Long> {

    // 특정 사용자의 가장 최신 제재 찾기
    Optional<PenaltyEntity> findTopByUserOrderByPenaltyAtDesc(UserEntity user);
}
