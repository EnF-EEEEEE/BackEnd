package com.enf.repository;

import com.enf.entity.LetterEntity;
import com.enf.entity.LetterStatusEntity;
import com.enf.entity.UserEntity;
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
      + "WHERE ls.letterStatusSeq = :letterStatusSeq")
  void updateLetterStatus(
      @Param("letterStatusSeq") Long letterStatusSeq,
      @Param("mentorLetter") LetterEntity mentorLetter
  );

  @Modifying
  @Transactional
  @Query("UPDATE letter_status ls "
      + "SET ls.isMenteeSaved = true WHERE ls.letterStatusSeq = :letterStatusSeq")
  void saveLetterForMentee(@Param("letterStatusSeq") Long letterStatusSeq);

  @Modifying
  @Transactional
  @Query("UPDATE letter_status ls "
      + "SET ls.isMentorSaved = true WHERE ls.letterStatusSeq = :letterStatusSeq")
  void saveLetterForMentor(@Param("letterStatusSeq") Long letterStatusSeq);

  Optional<LetterStatusEntity> findLetterStatusByLetterStatusSeq(Long letterStatusSeq);

  @Modifying
  @Transactional
  @Query("UPDATE letter_status ls "
      + "SET ls.isMenteeRead = true WHERE ls.letterStatusSeq = :letterStatusSeq")
  void updateIsMenteeRead(@Param("letterStatusSeq") Long letterStatusSeq);

  @Modifying
  @Transactional
  @Query("UPDATE letter_status ls "
      + "SET ls.isMentorRead = true WHERE ls.letterStatusSeq = :letterStatusSeq")
  void updateIsMentorRead(@Param("letterStatusSeq") Long letterStatusSeq);

  @Modifying
  @Transactional
  @Query("UPDATE letter_status ls "
      + "SET ls.mentor = :newMentor, ls.isMentorRead = false WHERE ls.letterStatusSeq = :letterStatusSeq")
  void updateMentor(@Param("letterStatusSeq") Long letterStatusSeq, @Param("newMentor")UserEntity newMentor);
}
