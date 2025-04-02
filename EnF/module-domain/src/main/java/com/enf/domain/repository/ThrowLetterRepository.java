package com.enf.domain.repository;

import com.enf.domain.entity.ThrowLetterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThrowLetterRepository extends JpaRepository<ThrowLetterEntity, Long> {

}
