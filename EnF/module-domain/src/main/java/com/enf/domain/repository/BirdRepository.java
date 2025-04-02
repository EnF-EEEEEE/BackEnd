package com.enf.domain.repository;

import com.enf.domain.entity.BirdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BirdRepository extends JpaRepository<BirdEntity, Long> {

  BirdEntity findByBirdName(String birdName);

  BirdEntity findByBirdSeq(Long birdSeq);
}
