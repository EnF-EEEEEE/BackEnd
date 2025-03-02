package com.enf.repository;

import com.enf.entity.LetterEntity;
import com.enf.entity.LetterStatusEntity;
import jakarta.transaction.Transactional;
import java.util.Optional;
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

  @Modifying
  @Transactional
  @Query("UPDATE letter_status ls "
      + "SET ls.isMenteeSaved = true WHERE ls.letterStatusSeq = :letterSeq")
  void saveLetterForMentee(@Param("letterSeq") Long letterSeq);

  @Modifying
  @Transactional
  @Query("UPDATE letter_status ls "
      + "SET ls.isMentorSaved = true WHERE ls.letterStatusSeq = :letterSeq")
  void saveLetterForMentor(@Param("letterSeq") Long letterSeq);

  Optional<LetterStatusEntity> findLetterStatusByLetterStatusSeq(Long letterSeq);
}
