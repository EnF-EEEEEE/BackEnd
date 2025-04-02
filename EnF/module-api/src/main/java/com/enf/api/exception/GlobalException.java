package com.enf.api.exception;

import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.domain.model.type.FailedResultType;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

  private final ResultResponse resultResponse;

  public GlobalException(FailedResultType failedResultCode) {
    super("");
    this.resultResponse = ResultResponse.of(failedResultCode);
  }

}
