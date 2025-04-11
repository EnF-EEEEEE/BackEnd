package com.enf.domain.model.dto.response.announcements;

import com.enf.domain.entity.AnnouncementsEntity;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementsDetails {

  private String title;

  private String content;

  private LocalDateTime createdAt;

  public static AnnouncementsDetails of(AnnouncementsEntity announcements) {
    return new AnnouncementsDetails(
        announcements.getTitle(),
        announcements.getContent(),
        announcements.getCreatedAt()
    );
  }

}
