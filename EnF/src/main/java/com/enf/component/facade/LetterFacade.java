package com.enf.component.facade;

import com.enf.entity.LetterEntity;
import com.enf.entity.NotificationEntity;
import com.enf.exception.GlobalException;
import com.enf.model.type.FailedResultType;
import com.enf.repository.LetterRepository;
import com.enf.repository.NotificationRepository;
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
   * 편지 저장
   *
   * @param letter 저장할 편지 엔티티
   */
  public void saveLetter(LetterEntity letter) {
    letterRepository.save(letter);
  }

  public LetterEntity findLetterByLetterSeq(Long letterSeq) {
    return letterRepository.findByLetterSeq(letterSeq)
        .orElseThrow(() -> new GlobalException(FailedResultType.LETTER_NOT_FOUND));
  }
}