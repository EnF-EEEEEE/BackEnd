package com.enf.domain.entity;

import com.enf.domain.model.type.AnnouncementsType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "announcements")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class AnnouncementsEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long announcementsSeq;

  @Enumerated(EnumType.STRING)
  private AnnouncementsType announcementsType;

  private String title;

  @Column(length = 1024)
  private String content;

  private LocalDateTime createdAt;

}
