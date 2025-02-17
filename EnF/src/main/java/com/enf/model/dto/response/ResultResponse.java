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

  // 요청 응답 시 반환할 데이터가 존재 할 경우
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

  // 요청 응답 시 반환할 데이터가 존재하지 않을 경우
  public static ResultResponse of(SuccessResultType successResultCode) {
    return new ResultResponse(successResultCode, null);
  }

  public static ResultResponse of(FailedResultType failedResultCode) {
    return new ResultResponse(failedResultCode, null);
  }

}
