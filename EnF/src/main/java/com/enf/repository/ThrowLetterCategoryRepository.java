package com.enf.repository;

import com.enf.entity.ThrowLetterCategoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThrowLetterCategoryRepository extends JpaRepository<ThrowLetterCategoryEntity, Long> {
  Optional<ThrowLetterCategoryEntity> findByThrowLetterCategorySeq(Long seq);

}
