package com.enf.domain.model.dto.request.notification;

import com.enf.domain.entity.LetterStatusEntity;
import com.enf.domain.entity.NotificationEntity;
import com.enf.domain.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 알림 정보를 담는 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDTO {

  private Long userSeq;           // 알림을 받을 사용자 ID
  private Long letterStatusSeq;   // 보낸 편지의 일련번호
  private String birdName;
  private String nickname;
  private String message;         // 알림 메시지
  private boolean isRead;



  /**
   * 멘티가 고마움 표시를 보냈을 때 생성되는 알림
   */
  public static NotificationDTO thanksToMentor(LetterStatusEntity letterStatus) {
    return new NotificationDTO(
        letterStatus.getMentor().getUserSeq(),
        letterStatus.getLetterStatusSeq(),
        letterStatus.getMentee().getBird().getBirdName(),
        letterStatus.getMentee().getNickname(),
        "님으로부터 나의 답장에 대한 고마움 표시가 도착했어요.",
        false

    );
  }

  /**
   * 멘티가 편지를 보냈을 때 생성되는 알림
   */
  public static NotificationDTO sendLetter(LetterStatusEntity letterStatus, UserEntity mentor) {
    return new NotificationDTO(
        mentor.getUserSeq(),
        letterStatus.getLetterStatusSeq(),
        letterStatus.getMentee().getBird().getBirdName(),
        letterStatus.getMentee().getNickname(),
        "님으로부터 날아온 편지를 확인해보세요",
        false
    );
  }

  /**
   * 멘토가 편지를 보냈을 때 생성되는 알림
   */
  public static NotificationDTO replyLetter(LetterStatusEntity letterStatus) {
    return new NotificationDTO(
        letterStatus.getMentee().getUserSeq(),
        letterStatus.getLetterStatusSeq(),
        letterStatus.getMentor().getBird().getBirdName(),
        letterStatus.getMentor().getNickname(),
        "님으로부터 날아온 답장을 확인해보세요",
        false
    );
  }

  /**
   * NotificationEntity → NotificationDTO 변환
   */
  public static NotificationDTO of(Long userSeq, NotificationEntity notification) {
    return new NotificationDTO(
        userSeq,
        notification.getLetterStatusSeq(),
        notification.getBirdName(),
        notification.getNickname(),
        notification.getMessage(),
        false
    );
  }

  /**
   * NotificationDTO → NotificationEntity 변환
   */
  public static NotificationEntity toEntity(NotificationDTO notification) {
    return NotificationEntity.builder()
        .userSeq(notification.getUserSeq())
        .letterStatusSeq(notification.getLetterStatusSeq())
        .birdName(notification.getBirdName())
        .nickname(notification.getNickname())
        .message(notification.getMessage())
        .isRead(notification.isRead())
        .createdAt(LocalDateTime.now())
        .build();
  }
}