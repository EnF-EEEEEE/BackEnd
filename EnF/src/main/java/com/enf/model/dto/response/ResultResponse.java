package com.enf.model.dto.response;


import com.enf.model.type.FailedResultType;
import com.enf.model.type.SuccessResultType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResultResponse {

  private final Integer code;
  private final HttpStatus status;
  private final String message;
  private final Object data;

  public ResultResponse(SuccessResultType successResultCode, Object data) {
    this.code = successResultCode.getStatus().value();
    this.status = successResultCode.getStatus();
    this.message = successResultCode.getMessage();
    this.data = data;
  }

  public ResultResponse(FailedResultType failedResultCode,  Object data) {
    this.code = failedResultCode.getStatus().value();
    this.status = failedResultCode.getStatus();
    this.message = failedResultCode.getMessage();
    this.data = data;
  }

  public static ResultResponse of(SuccessResultType successResultCode) {
    return new ResultResponse(successResultCode, null);
  }

  public static ResultResponse of(FailedResultType failedResultCode) {
    return new ResultResponse(failedResultCode, null);
  }

}
