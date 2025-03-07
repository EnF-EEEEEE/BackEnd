package com.enf.repository;

import com.enf.entity.BirdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BirdRepository extends JpaRepository<BirdEntity, Long> {

  BirdEntity findByBirdName(String birdName);

  BirdEntity findByBirdSeq(Long birdSeq);
}
