package com.enf.domain.model.dto.response.announcements;

import com.enf.domain.entity.AnnouncementsEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Announcements {

  private Long announcementSeq;

  private String title;

  private LocalDateTime createAt;

  public static List<Announcements> of(List<AnnouncementsEntity> list) {
    List<Announcements> announcementsList = new ArrayList<>();

    if(list == null || list.isEmpty()) {
      return new ArrayList<>();
    }

    for (AnnouncementsEntity announcement : list) {
      announcementsList.add(new Announcements(
          announcement.getAnnouncementsSeq(),
          announcement.getTitle(),
          announcement.getCreatedAt()
      ));
    }
    return announcementsList;
  }

}
