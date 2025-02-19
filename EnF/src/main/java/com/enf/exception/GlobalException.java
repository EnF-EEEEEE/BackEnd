package com.enf.exception;

import com.enf.model.dto.response.ResultResponse;
import com.enf.model.type.FailedResultType;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

  private final ResultResponse resultResponse;

  public GlobalException(FailedResultType failedResultCode) {
    super("");
    this.resultResponse = ResultResponse.of(failedResultCode);
  }

}
