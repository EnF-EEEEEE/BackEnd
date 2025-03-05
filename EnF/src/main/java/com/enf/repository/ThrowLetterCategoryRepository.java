package com.enf.repository;

import com.enf.entity.ThrowLetterCategoryEntity;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ThrowLetterCategoryRepository extends JpaRepository<ThrowLetterCategoryEntity, Long> {
  Optional<ThrowLetterCategoryEntity> findByThrowLetterCategorySeq(Long seq);

}
