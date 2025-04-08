package com.enf.api.component.badword;

import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.domain.model.type.FailedResultType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class BadWordExceptionHandler {

    @ExceptionHandler(BadWordException.class)
    public ResponseEntity<ResultResponse> handleBadWordException(BadWordException ex) {

        BadWordExceptionDTO dto = new BadWordExceptionDTO();
        dto.setBadWord(ex.getBadWord());
        if (ex.getFieldName() != null) {
            dto.setFieldName(ex.getFieldName());
        }
        ResultResponse response = new ResultResponse(FailedResultType.BAD_WORD_DENIED, dto);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
