package com.enf.domain.model.dto.request.announcements;

import com.enf.domain.entity.AnnouncementsEntity;
import com.enf.domain.model.type.AnnouncementsType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AnnouncementsDTO {

  @JsonProperty("type")
  private String type;

  @JsonProperty("title")
  private String title;

  @JsonProperty("content")
  private String content;

  @JsonCreator
  public AnnouncementsDTO(String type, String title, String content) {
    this.type = type;
    this.title = title;
    this.content = content;
  }


  public static AnnouncementsEntity of(AnnouncementsDTO dto) {
    return AnnouncementsEntity.builder()
        .title(dto.getTitle())
        .content(dto.getContent())
        .announcementsType(AnnouncementsType.valueOf(dto.getType()))
        .createdAt(LocalDateTime.now())
        .build();
  }
}
