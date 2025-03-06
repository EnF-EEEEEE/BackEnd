package com.enf.model.type;

import lombok.Getter;

@Getter
public enum LetterListType {
  ALL("all"), PENDING("pending"), SAVE("save");

  private final String value;

  LetterListType(String value) {
    this.value = value;
  }
}
