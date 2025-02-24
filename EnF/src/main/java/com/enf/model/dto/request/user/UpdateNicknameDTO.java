package com.enf.model.dto.request.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UpdateNicknameDTO {

  @JsonProperty("nickname")
  private String nickname;

  @JsonCreator
  public UpdateNicknameDTO(String nickname) {
    this.nickname = nickname;
  }
}
