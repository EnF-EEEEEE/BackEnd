package com.enf.model.dto.request.notification;

import com.enf.entity.LetterStatusEntity;
import com.enf.entity.NotificationEntity;
import com.enf.entity.UserEntity;
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
  private String sendUser;        // 알림을 보낸 사용자 (없을 수도 있음)
  private String message;         // 알림 메시지



  /**
   * 멘티가 고마움 표시를 보냈을 때 생성되는 알림
   */
  public static NotificationDTO thanksToMentor(LetterStatusEntity letterStatus) {
    return new NotificationDTO(
        letterStatus.getMentor().getUserSeq(),
        letterStatus.getLetterStatusSeq(),
        letterStatus.getMentee().getNickname(),
        letterStatus.getMentee().getNickname() + "님으로부터 나의 답장에 대한 고마움 표시가 도착했어요."

    );
  }

  /**
   * 멘티가 편지를 보냈을 때 생성되는 알림
   */
  public static NotificationDTO sendLetter(
      LetterStatusEntity letterStatus, UserEntity mentor) {
    return new NotificationDTO(
        mentor.getUserSeq(),
        letterStatus.getLetterStatusSeq(),
        letterStatus.getMentee().getNickname(),
        letterStatus.getMentee().getNickname() + "님으로부터 날아온 답장을 확인해보세요"
    );
  }

  /**
   * 멘토가 편지를 보냈을 때 생성되는 알림
   */
  public static NotificationDTO replyLetter(LetterStatusEntity letterStatus) {
    return new NotificationDTO(
        letterStatus.getMentee().getUserSeq(),
        letterStatus.getLetterStatusSeq(),
        letterStatus.getMentor().getNickname(),
        letterStatus.getMentor().getNickname() + "님으로부터 날아온 답장을 확인해보세요"
    );
  }

  /**
   * NotificationEntity → NotificationDTO 변환
   */
  public static NotificationDTO of(Long userSeq, NotificationEntity notification) {
    return new NotificationDTO(
        userSeq,
        notification.getLetterStatusSeq(),
        notification.getSendUser(),
        notification.getMessage()
    );
  }

  /**
   * NotificationDTO → NotificationEntity 변환
   */
  public static NotificationEntity toEntity(NotificationDTO notification) {
    return NotificationEntity.builder()
        .userSeq(notification.getUserSeq())
        .letterStatusSeq(notification.getLetterStatusSeq())
        .sendUser(notification.getSendUser())
        .message(notification.getMessage())
        .createdAt(LocalDateTime.now())
        .build();
  }
}