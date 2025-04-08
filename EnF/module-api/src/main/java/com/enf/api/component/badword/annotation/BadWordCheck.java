package com.enf.api.component.badword.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 비속어 필터링을 위한 어노테이션 만들기
 * 메서드 파라미터 및 필드에 적용 가능합니다.
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BadWordCheck {
    /**
     * 비속어가 발견되었을 때의 오류 메시지
     */
    String message() default "부적절한 표현이 포함되어 있습니다.";

    /**
     * 공백을 제거하고 검사할지 여부
     */
    boolean ignoreBlank() default false;
}