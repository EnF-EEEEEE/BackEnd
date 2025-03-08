package com.enf.model.dto.response.notification;

import com.enf.entity.NotificationEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

  private Long letterStatusSeq;

  private String birdName;

  private String nickname;

  private String message;

  private boolean read;

  private LocalDateTime createAt;

  public static List<Notification> of(List<NotificationEntity> list) {
    List<Notification> notifications = new ArrayList<>();

    for (NotificationEntity notification : list) {
      notifications.add(new Notification(
          notification.getLetterStatusSeq(),
          notification.getBirdName(),
          notification.getNickname(),
          notification.getMessage(),
          notification.isRead(),
          notification.getCreatedAt()
      ));
    }
    return notifications;
  }
}
