package com.enf.model.dto.request.letter;

import com.enf.entity.LetterEntity;
import com.enf.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReceiveLetterDTO {

  @JsonProperty("categoryName")
  private String categoryName;

  @JsonProperty("receiveUser")
  private String receiveUser;

  @JsonProperty("title")
  private String title;

  @JsonProperty("letter")
  private String letter;

  @JsonCreator
  public ReceiveLetterDTO(String categoryName, String receiveUser, String title, String letter) {
    this.categoryName = categoryName;
    this.receiveUser = receiveUser;
    this.title = title;
    this.letter = letter;
  }

  public static LetterEntity of(UserEntity sendUser,
      UserEntity receiveUser, ReceiveLetterDTO receiveLetterDTO) {

    return LetterEntity.builder()
        .sendUser(sendUser)
        .receiveUser(receiveUser)
        .categoryName(receiveLetterDTO.getCategoryName())
        .letterTitle(receiveLetterDTO.getTitle())
        .letter(receiveLetterDTO.getLetter())
        .createAt(LocalDateTime.now())
        .build();
  }
}
