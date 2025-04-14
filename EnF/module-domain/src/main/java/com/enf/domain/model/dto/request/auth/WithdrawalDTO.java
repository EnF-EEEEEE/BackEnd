package com.enf.domain.model.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class WithdrawalDTO {

  @JsonProperty("withdrawalType")

  private String withdrawalType;

  @JsonCreator
  public WithdrawalDTO(String withdrawalType) {
    this.withdrawalType = withdrawalType;
  }
}
