package com.enf.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FailedResultType {
  ;


  private final HttpStatus status;
  private final String message;

}
