package com.enf.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "notification")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class NotificationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long notificationSeq;

  private Long userSeq;

  private Long letterStatusSeq;

  private String birdName;

  private String nickname;

  private String message;

  private boolean isRead;

  private LocalDateTime createdAt;

}
