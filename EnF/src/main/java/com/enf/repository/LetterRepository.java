package com.enf.repository;

import com.enf.entity.LetterEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterRepository extends JpaRepository<LetterEntity, Long> {

  Optional<LetterEntity> findByLetterSeq(Long letterSeq);

}
