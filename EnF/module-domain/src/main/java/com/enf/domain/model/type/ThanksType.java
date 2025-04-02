package com.enf.domain.model.type;

import lombok.Getter;

@Getter
public enum ThanksType {

  MOVED("정성어린 답장에 감동 받았어요!"),
  HELPFUL("편지 내용이 도움이 되었어요!"),
  NOT_ALONE("혼자가 아닌 것 같아 기뻐요!");

  private final String text;

  ThanksType(String value) {
    this.text = value;
  }
}
