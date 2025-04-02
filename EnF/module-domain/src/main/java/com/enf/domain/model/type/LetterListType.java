package com.enf.domain.model.type;

import lombok.Getter;

@Getter
public enum LetterListType {
  ALL("all"), PENDING("pending"), ARCHIVE("archive");

  private final String value;

  LetterListType(String value) {
    this.value = value;
  }
}
