package com.enf.repository;

import com.enf.entity.LetterStatusEntity;
import com.enf.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LetterStatusRepository extends JpaRepository<LetterStatusEntity, Long> {

    // 멘티가 보낸 편지 조회 쿼리 - LEFT JOIN으로 변경하여 null 값 처리
    @Query("SELECT ls FROM letter_status ls " +
            "JOIN ls.mentee m " +
            "JOIN m.role r " +
            "JOIN ls.menteeLetter ml " +  // INNER JOIN으로 사용하여 menteeLetter가 null이 아닌 경우만 조회
            "LEFT JOIN ls.mentorLetter mtl " +  // LEFT JOIN으로 변경하여 답장이 없는 경우도 조회
            "WHERE r.roleSeq = 4 " +  // MENTEE 역할 (roleSeq = 4)
            "ORDER BY ml.createAt DESC")
    Page<LetterStatusEntity> findAllMenteeLetters(Pageable pageable);

    List<LetterStatusEntity> findByMenteeUserSeq(Long userSeq);


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
}
