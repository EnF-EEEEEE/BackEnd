package com.enf.model.dto.request.letter;

import com.enf.entity.LetterEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReplyLetterDTO {

  @JsonProperty("letterStatusSeq")
  private Long letterStatusSeq;

  @JsonProperty("categoryName")
  private String categoryName;

  @JsonProperty("title")
  private String title;

  @JsonProperty("letter")
  private String letter;

  @JsonCreator
  public ReplyLetterDTO(Long letterStatusSeq, String categoryName, String title, String letter) {
    this.letterStatusSeq = letterStatusSeq;
    this.categoryName = categoryName;
    this.title = title;
    this.letter = letter;
  }

  public static LetterEntity of(ReplyLetterDTO replyLetter) {

    return LetterEntity.builder()
        .categoryName(replyLetter.getCategoryName())
        .letterTitle(replyLetter.getTitle())
        .letter(replyLetter.getLetter())
        .createAt(LocalDateTime.now())
        .build();
  }
}
