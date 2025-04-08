package com.enf.api.component.badword;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 비속어가 발견되었을 때 400(Bad Request)를 보냄
 */
@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadWordException extends RuntimeException {

    private final String fieldName;
    private final String badWord;

    public BadWordException(String message) {
        super(message);
        this.fieldName = null;
        this.badWord = null;
    }

    public BadWordException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
        this.badWord = null;
    }

    public BadWordException(String fieldName, String badWord, String message) {
        super(message);
        this.fieldName = fieldName;
        this.badWord = badWord;
    }
}