package com.enf.domain.repository;

import com.enf.domain.entity.LetterEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterRepository extends JpaRepository<LetterEntity, Long> {

}
