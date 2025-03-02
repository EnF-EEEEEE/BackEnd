package com.enf.repository;

import com.enf.entity.LetterEntity;
import com.enf.entity.LetterStatusEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterStatusRepository extends JpaRepository<LetterStatusEntity, Long> {

  @Modifying
  @Transactional
  @Query("UPDATE letter_status ls "
      + "SET ls.mentorLetter = :mentorLetter, ls.createAt = CURRENT_TIMESTAMP "
      + "WHERE ls.menteeLetter = :menteeLetter")
  void updateLetterStatus(
      @Param("mentorLetter") LetterEntity mentorLetter,
      @Param("menteeLetter") LetterEntity menteeLetter
  );
}
