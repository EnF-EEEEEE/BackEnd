package com.enf.ut;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UTDTO {

  @JsonProperty("nickname")
  private String nickname;

  @JsonProperty("categoryName")
  private String categoryName;

  @JsonProperty("title")
  private String title;

  @JsonProperty("content")
  private String content;

  @JsonCreator
  public UTDTO(String nickname, String categoryName, String title, String content) {
    this.nickname = nickname;
    this.categoryName = categoryName;
    this.title = title;
    this.content = content;
  }
}
