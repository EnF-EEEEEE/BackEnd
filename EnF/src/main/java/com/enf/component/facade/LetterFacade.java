package com.enf.component.facade;

import com.enf.entity.LetterEntity;
import com.enf.entity.LetterStatusEntity;
import com.enf.entity.NotificationEntity;
import com.enf.entity.ThrowLetterEntity;
import com.enf.entity.UserEntity;
import com.enf.exception.GlobalException;
import com.enf.model.dto.request.letter.ReplyLetterDTO;
import com.enf.model.dto.response.PageResponse;
import com.enf.model.dto.response.letter.LetterDetailsDTO;
import com.enf.model.dto.response.letter.ReceiveLetterDTO;
import com.enf.model.type.FailedResultType;
import com.enf.model.type.LetterListType;
import com.enf.repository.LetterRepository;
import com.enf.repository.LetterStatusRepository;
import com.enf.repository.NotificationRepository;
import com.enf.repository.ThrowLetterRepository;
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
  private final ThrowLetterRepository throwLetterRepository;

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
  public LetterStatusEntity saveMenteeLetter(LetterEntity letter, UserEntity mentee, UserEntity mentor) {
    // 편지를 저장
    LetterEntity menteeLetter = letterRepository.save(letter);

    // 편지 상태 저장 (멘토가 답장할 때 참조할 수 있도록 함)
    return letterStatusRepository.save(
        LetterStatusEntity.builder()
            .mentee(mentee)
            .mentor(mentor)
            .menteeLetter(menteeLetter)
            .isMenteeRead(false)
            .isMentorRead(false)
            .isMenteeSaved(false)
            .isMentorSaved(false)
            .isThanksToMentor(false)
            .createAt(LocalDateTime.now())
            .build()
    );
  }

  /**
   * 멘토가 답장을 보낼 때 편지 저장 및 상태 업데이트
   *
   * @param letterStatus 저장할 멘토의 답장 편지 엔티티
   * @param replyLetter 멘티가 보낸 원본 편지 엔티티
   */
  public void saveMentorLetter(LetterStatusEntity letterStatus, ReplyLetterDTO replyLetter) {
    LetterEntity letter = ReplyLetterDTO
        .of(replyLetter, letterStatus.getMenteeLetter().getCategoryName());
    // 멘토의 답장 편지를 저장
    LetterEntity mentorLetter = letterRepository.save(letter);

    // 기존 편지 상태 업데이트 (멘토의 답장을 반영)
    letterStatusRepository.updateLetterStatus(letterStatus.getLetterStatusSeq(), mentorLetter);
  }

  /**
   * 특정 사용자의 모든 편지를 조회하는 기능 (멘티와 멘토 구분하여 처리)
   *
   * 1. 사용자 정보를 기반으로 해당 사용자가 보낸 또는 받은 편지를 조회한다.
   * 2. LetterListType을 활용하여 전체, 미응답, 저장된 편지 유형을 필터링할 수 있다.
   * 3. QueryDSL 기반의 `letterQueryRepository`를 통해 데이터 조회 후 페이징 처리한다.
   *
   * @param user        현재 로그인한 사용자 (멘티 또는 멘토)
   * @param pageNumber  요청한 페이지 번호
   * @param letterListType 조회할 편지 유형 (ALL, PENDING, SAVE)
   * @return 페이지네이션이 적용된 편지 목록
   */
  public PageResponse<ReceiveLetterDTO> getLetterList(UserEntity user,
      int pageNumber, LetterListType letterListType) {

    return letterQueryRepository.getLetterList(user, pageNumber, letterListType.getValue());
  }

  /**
   * 특정 사용자가 편지를 저장하는 기능
   *
   * 1. 사용자의 역할(멘티 또는 멘토)을 확인하여 적절한 저장 로직을 실행한다.
   * 2. 멘티라면 `saveLetterForMentee()` 메서드를 호출하여 저장 처리한다.
   * 3. 멘토라면 `saveLetterForMentor()` 메서드를 호출하여 저장 처리한다.
   * 4. 해당 편지가 성공적으로 저장되었음을 나타내는 응답을 반환한다.
   *
   * @param user      현재 로그인한 사용자 (멘티 또는 멘토)
   * @param letterStatusSeq 저장할 편지의 고유 식별자 (ID)
   */
  public void saveLetter(UserEntity user, Long letterStatusSeq) {
    boolean isMentee = user.getRole().getRoleName().equals("MENTEE");

    if (isMentee) {
      letterStatusRepository.saveLetterForMentee(letterStatusSeq);
    } else {
      letterStatusRepository.saveLetterForMentor(letterStatusSeq);
    }
  }

  /**
   * 특정 편지 상태 정보를 조회
   *
   * @param letterStatusSeq 편지 상태 ID
   * @return 조회된 LetterStatusEntity 객체
   * @throws GlobalException 편지가 존재하지 않을 경우 예외 발생
   */
  public LetterStatusEntity getLetterStatus(Long letterStatusSeq) {
    return letterStatusRepository
        .findLetterStatusByLetterStatusSeq(letterStatusSeq)
        .orElseThrow(() -> new GlobalException(FailedResultType.LETTER_NOT_FOUND));
  }

  /**
   * 편지 상세 정보 조회 메서드
   *
   * 사용자의 역할(멘티/멘토)에 따라 다른 DTO 변환 로직 수행
   *
   * @param user      요청한 사용자 (멘티 또는 멘토)
   * @param letterStatus 조회할 편지
   * @return 편지 상세 정보를 포함하는 LetterDetailsDTO
   * @throws GlobalException 편지를 찾을 수 없는 경우 LETTER_NOT_FOUND 예외 발생
   */
  public LetterDetailsDTO getLetterDetails(UserEntity user, LetterStatusEntity letterStatus) {

    boolean isMentee = user.getRole().getRoleName().equals("MENTEE");

    if (isMentee) {
      if (!letterStatus.isMenteeRead()) {
        letterStatusRepository.updateIsMenteeRead(letterStatus.getLetterStatusSeq());
      }
    } else {
      if (!letterStatus.isMentorRead()) {
        letterStatusRepository.updateIsMentorRead(letterStatus.getLetterStatusSeq());
      }
    }

    return user.getRole().getRoleName().equals("MENTEE")
        ? LetterDetailsDTO.ofMentee(letterStatus)
        : LetterDetailsDTO.ofMentor(letterStatus);
  }

  /**
   * 편지를 새로운 멘토에게 전달하는 메서드
   *
   * @param letterStatus  현재 편지의 상태 정보
   */
  public void throwLetter(LetterStatusEntity letterStatus) {
    ThrowLetterEntity throwLetterEntity = ThrowLetterEntity.builder()
        .letterStatus(letterStatus)
        .throwUser(letterStatus.getMentor())
        .build();
    throwLetterRepository.save(throwLetterEntity);
  }

  /**
   * 편지의 멘토를 새로운 멘토로 변경
   *
   * @param letterStatus 현재 편지의 상태 정보
   * @param newMentor 새로운 멘토 정보
   */
  public void updateMentor(LetterStatusEntity letterStatus, UserEntity newMentor) {
    letterStatusRepository.updateMentor(letterStatus.getLetterStatusSeq(), newMentor);
  }

  /**
   * 고마움 전달을 위한 메서드
   *
   * @param letterSeq 고마움을 전달할 멘토 편지의 고유 식별자
   */
  public LetterStatusEntity thanksToMentor(Long letterSeq) {
    LetterStatusEntity letterStatus = letterStatusRepository
        .getLetterStatusByMentorLetterLetterSeq(letterSeq);

    letterStatusRepository.updateIsThankToMentor(letterStatus.getLetterStatusSeq());

    return letterStatus;
  }
}