package com.enf.model.dto.request.letter;

import com.enf.entity.LetterEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class SendLetterDTO {

  @JsonProperty("birdName")
  private String birdName;

  @JsonProperty("categoryName")
  private String categoryName;

  @JsonProperty("title")
  private String title;

  @JsonProperty("letter")
  private String letter;

  @JsonCreator
  public SendLetterDTO(String birdName, String categoryName, String title, String letter) {
    this.birdName = birdName;
    this.categoryName = categoryName;
    this.title = title;
    this.letter = letter;
  }


  public static LetterEntity of(SendLetterDTO sendLetter) {
    return LetterEntity.builder()
        .birdName(sendLetter.birdName)
        .categoryName(sendLetter.getCategoryName())
        .letterTitle(sendLetter.getTitle())
        .letter(sendLetter.getLetter())
        .createAt(LocalDateTime.now())
        .build();
  }

}
