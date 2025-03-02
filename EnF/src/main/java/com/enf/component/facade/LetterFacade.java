package com.enf.component.facade;

import com.enf.entity.LetterEntity;
import com.enf.entity.LetterStatusEntity;
import com.enf.entity.NotificationEntity;
import com.enf.entity.UserEntity;
import com.enf.exception.GlobalException;
import com.enf.model.dto.response.PageResponse;
import com.enf.model.dto.response.letter.ReceiveLetterDTO;
import com.enf.model.type.FailedResultType;
import com.enf.repository.LetterRepository;
import com.enf.repository.LetterStatusRepository;
import com.enf.repository.NotificationRepository;
import com.enf.repository.querydsl.LetterQueryRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LetterFacade {

  private final LetterRepository letterRepository;
  private final NotificationRepository notificationRepository;
  private final LetterStatusRepository letterStatusRepository;
  private final LetterQueryRepository letterQueryRepository;

  /**
   * 특정 사용자의 모든 알림 조회
   *
   * @param userSeq 사용자 ID
   * @return 사용자의 모든 알림 리스트
   */
  public List<NotificationEntity> findAllByUserSeq(Long userSeq) {
    return notificationRepository.findAllByUserSeq(userSeq);
  }

  /**
   * 특정 사용자의 모든 알림 삭제
   *
   * @param userSeq 사용자 ID
   */
  public void deleteAllByUserSeq(Long userSeq) {
    notificationRepository.deleteAllByUserSeq(userSeq);
  }

  /**
   * 알림 저장
   *
   * @param notification 저장할 알림 엔티티
   */
  public void saveNotification(NotificationEntity notification) {
    notificationRepository.save(notification);
  }

  /**
   * 멘티가 보낸 편지를 저장하고 편지 상태(LetterStatusEntity)도 함께 저장
   *
   * @param letter 저장할 편지 엔티티
   * @param mentee 편지를 보낸 멘티
   * @param mentor 편지를 받는 멘토
   */
  public void saveMenteeLetter(LetterEntity letter, UserEntity mentee, UserEntity mentor) {
    // 편지를 저장
    LetterEntity menteeLetter = letterRepository.save(letter);

    // 편지 상태 저장 (멘토가 답장할 때 참조할 수 있도록 함)
    letterStatusRepository.save(
        LetterStatusEntity.builder()
            .mentee(mentee)
            .mentor(mentor)
            .menteeLetter(menteeLetter)
            .createAt(LocalDateTime.now())
            .build()
    );
  }

  /**
   * 멘토가 답장을 보낼 때 편지 저장 및 상태 업데이트
   *
   * @param letter 저장할 멘토의 답장 편지 엔티티
   * @param menteeLetter 멘티가 보낸 원본 편지 엔티티
   */
  public void saveMentorLetter(LetterEntity letter, LetterEntity menteeLetter) {
    // 멘토의 답장 편지를 저장
    LetterEntity mentorLetter = letterRepository.save(letter);

    // 기존 편지 상태 업데이트 (멘토의 답장을 반영)
    letterStatusRepository.updateLetterStatus(mentorLetter, menteeLetter);
  }

  /**
   * 특정 편지 ID로 편지 조회
   *
   * @param letterSeq 조회할 편지의 ID
   * @return 조회된 LetterEntity 객체
   * @throws GlobalException 편지가 존재하지 않을 경우 예외 발생
   */
  public LetterEntity findLetterByLetterSeq(Long letterSeq) {
    return letterRepository.findByLetterSeq(letterSeq)
        .orElseThrow(() -> new GlobalException(FailedResultType.LETTER_NOT_FOUND));
  }

  /**
   * 특정 사용자의 모든 편지 조회 (멘티와 멘토 구분하여 처리)
   *
   * @param user 사용자 엔티티 (멘티 또는 멘토)
   * @param pageNumber 요청한 페이지 번호
   * @return 페이지네이션된 편지 목록
   */
  public PageResponse<ReceiveLetterDTO> getAllLetterList(UserEntity user, int pageNumber) {
    // 사용자가 멘티인 경우 멘티의 편지 리스트 반환
    if (user.getRole().getRoleName().equals("MENTEE")) {
      return letterQueryRepository.getAllLetterListForMenTee(user, pageNumber);
    }

    // 사용자가 멘토인 경우 멘토의 편지 리스트 반환
    return letterQueryRepository.getAllLetterListForMentor(user, pageNumber);
  }

  /**
   * 미응답(답장이 없는) 편지 목록을 조회하는 기능 (페이징 지원)
   * 1. 사용자의 정보를 받아서 해당 사용자가 보낸 미응답 편지 목록을 조회
   * 2. 페이징 처리 후 결과 반환
   *
   * @param user       조회할 사용자 (멘토 또는 멘티)
   * @param pageNumber 요청한 페이지 번호
   * @return 미응답 편지 리스트 (페이지네이션 적용)
   */
  public PageResponse<ReceiveLetterDTO> getPendingLetterList(UserEntity user, int pageNumber) {
    return letterQueryRepository.getPendingLetterList(user, pageNumber);
  }
}