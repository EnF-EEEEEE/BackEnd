package com.enf.model.dto.request.notification;

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

  private Long userSeq;    // 알림을 받을 사용자 ID
  private String sendUser; // 알림을 보낸 사용자 (없을 수도 있음)
  private String message;  // 알림 메시지


  /**
   * SSE 구독 성공 시 생성되는 알림
   */
  public static NotificationDTO subscribe(UserEntity user) {
    return new NotificationDTO(
        user.getUserSeq(),
        null,
        user.getNickname() + " 버디 구독 성공!"
    );
  }

  /**
   * 멘티가 편지를 보냈을 때 생성되는 알림
   */
  public static NotificationDTO sendLetter(UserEntity mentee, UserEntity mentor) {
    return new NotificationDTO(
        mentor.getUserSeq(),
        mentee.getNickname(),
        mentee.getNickname() + " 버디가 편지를 보냈어요~"
    );
  }

  /**
   * 멘토가 편지를 보냈을 때 생성되는 알림
   */
  public static NotificationDTO replyLetter(UserEntity mentor, UserEntity mentee) {
    return new NotificationDTO(
        mentee.getUserSeq(),
        mentor.getNickname(),
        mentor.getNickname() + " 버디가 편지를 보냈어요~"
    );
  }

  /**
   * NotificationEntity → NotificationDTO 변환
   */
  public static NotificationDTO of(Long userSeq, NotificationEntity notification) {
    return new NotificationDTO(
        userSeq,
        notification.getSendUser(),
        notification.getMessage()
    );
  }

  /**
   * 여러 개의 알림을 묶어서 보낼 때 사용
   */
  public static NotificationDTO of(Long userSeq, NotificationEntity notification, int size) {
    return new NotificationDTO(
        userSeq,
        notification.getSendUser(),
        notification.getSendUser() + "외 " + size + "명의 버디가 편지를 보냈어요~"
    );
  }

  /**
   * NotificationDTO → NotificationEntity 변환
   */
  public static NotificationEntity toEntity(NotificationDTO notification) {
    return NotificationEntity.builder()
        .userSeq(notification.getUserSeq())
        .sendUser(notification.getSendUser())
        .message(notification.getMessage())
        .createdAt(LocalDateTime.now())
        .build();
  }
}