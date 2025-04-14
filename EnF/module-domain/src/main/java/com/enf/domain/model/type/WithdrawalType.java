package com.enf.domain.model.type;

import lombok.Getter;

@Getter
public enum WithdrawalType {

  NO_DESIRED_ACTIVITY("원하는 활동이 없어요"),
  NOT_HELPFUL_FOR_PROBLEMS("고민을 해결하는 데 도움이 안돼요"),
  INCONVENIENT_SERVICE("서비스를 이용하기가 불편해요"),
  OTHER("기타");

  private final String value;

  WithdrawalType(String value) {
    this.value = value;
  }
}
