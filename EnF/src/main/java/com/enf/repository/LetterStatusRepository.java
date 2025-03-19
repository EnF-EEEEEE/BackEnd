package com.enf.repository;

import com.enf.entity.LetterEntity;
import com.enf.entity.LetterStatusEntity;
import com.enf.entity.UserEntity;
import com.enf.model.type.ThanksType;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
      + "SET ls.mentorLetter = :mentorLetter, "
      + "ls.createAt = CURRENT_TIMESTAMP, "
      + "ls.isMentorRead = true, "
      + "ls.isMenteeRead = false "
      + "WHERE ls.letterStatusSeq = :letterStatusSeq")
  void saveMentorLetter(
      @Param("letterStatusSeq") Long letterStatusSeq,
      @Param("mentorLetter") LetterEntity mentorLetter
  );

  @Modifying
  @Transactional
  @Query("UPDATE letter_status ls SET "
      + "ls.isMenteeSaved = CASE "
      + "WHEN :roleName = 'MENTEE' THEN CASE WHEN ls.isMenteeSaved = true THEN false ELSE true END "
      + "ELSE ls.isMenteeSaved END, "
      + "ls.isMentorSaved = CASE "
      + "WHEN :roleName = 'MENTOR' THEN  CASE WHEN ls.isMentorSaved = true THEN false ELSE true END "
      + "ELSE ls.isMentorSaved END "
      + "WHERE ls.letterStatusSeq = :letterStatusSeq")
  void archiveLetterForUser(@Param("letterStatusSeq") Long letterStatusSeq, @Param("roleName") String roleName);


  Optional<LetterStatusEntity> findLetterStatusByLetterStatusSeq(Long letterStatusSeq);

  @Modifying
  @Transactional
  @Query("UPDATE letter_status ls " +
      "SET ls.isMenteeRead = CASE WHEN :roleName = 'MENTEE' THEN true ELSE ls.isMenteeRead END, " +
      "    ls.isMentorRead = CASE WHEN :roleName = 'MENTOR' THEN true ELSE ls.isMentorRead END " +
      "WHERE ls.letterStatusSeq = :letterStatusSeq")
  void updateLetterReadStatus(@Param("letterStatusSeq") Long letterStatusSeq, @Param("roleName") String roleName);

  @Modifying
  @Transactional
  @Query("UPDATE letter_status ls "
      + "SET ls.mentor = :newMentor, ls.isMentorRead = false WHERE ls.letterStatusSeq = :letterStatusSeq")
  void changeMentor(@Param("letterStatusSeq") Long letterStatusSeq,
      @Param("newMentor") UserEntity newMentor);

  @Modifying
  @Transactional
  @Query("UPDATE letter_status ls "
      + "SET ls.thanksType = :thanksType, "
      + "ls.isMentorRead = false "
      + "WHERE ls.letterStatusSeq =:letterStatusSeq")
  void thankToMentor(Long letterStatusSeq, ThanksType thanksType);

  LetterStatusEntity getLetterStatusByMentorLetterLetterSeq(Long letterSeq);

  // 멘티가 보낸 편지 조회 쿼리 - LEFT JOIN으로 변경하여 null 값 처리
  @Query("SELECT ls FROM letter_status ls " +
          "JOIN ls.mentee m " +
          "JOIN m.role r " +
          "JOIN ls.menteeLetter ml " +  // INNER JOIN으로 사용하여 menteeLetter가 null이 아닌 경우만 조회
          "LEFT JOIN ls.mentorLetter mtl " +  // LEFT JOIN으로 변경하여 답장이 없는 경우도 조회
          "WHERE r.roleSeq = 4 " +  // MENTEE 역할 (roleSeq = 4)
          "ORDER BY ml.createAt DESC")
  Page<LetterStatusEntity> findAllMenteeLetters(Pageable pageable);


  /**
   * 특정 멘티가 특정 기간 내에 작성한 편지가 존재하는지 확인
   * @param mentee 멘티 사용자
   * @param startDateTime 조회 시작 일시
   * @param endDateTime 조회 종료 일시
   * @return 편지 존재 여부
   */
  boolean existsByMenteeAndMenteeLetterIsNotNullAndCreateAtBetween(
          UserEntity mentee, LocalDateTime startDateTime, LocalDateTime endDateTime);


  LetterStatusEntity findByLetterStatusSeq(Long letterStatusSeq);

  @Query("SELECT EXISTS (SELECT 1 FROM letter_status lst " +
      "WHERE (:roleName = 'MENTEE' AND lst.mentee.userSeq = :userSeq AND lst.isMenteeRead = false) OR " +
      "      (:roleName = 'MENTOR' AND lst.mentor.userSeq = :userSeq AND lst.isMentorRead = false))")
  Boolean existsReadStatus(@Param("userSeq") Long userSeq, @Param("roleName") String roleName);


  @Query("SELECT COUNT(lst) FROM letter_status lst WHERE " +
      "(:roleName = 'MENTEE' AND lst.mentee = :user AND lst.menteeLetter IS NOT NULL) OR " +
      "(:roleName = 'MENTOR' AND lst.mentor = :user AND lst.mentorLetter IS NOT NULL)")
  int countSendLettersByUser(@Param("user") UserEntity user, @Param("roleName") String roleName);

  @Query("SELECT COUNT(lst) FROM letter_status lst WHERE " +
      "(:roleName = 'MENTEE' AND lst.mentee = :user AND lst.mentorLetter IS NOT NULL) OR " +
      "(:roleName = 'MENTOR' AND lst.mentor = :user AND lst.menteeLetter IS NOT NULL)")
  int countReplyLettersByUser(@Param("user") UserEntity user, @Param("roleName") String roleName);
}
