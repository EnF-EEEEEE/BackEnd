package com.enf.api.component.badword;

import com.enf.api.component.badword.annotation.BadWordCheck;
import com.enf.api.exception.GlobalException;
import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.domain.model.type.FailedResultType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
public class BadWordAspect {

    private final BadWordFiltering badWordFiltering;

    public BadWordAspect(@Lazy BadWordFiltering badWordFiltering) {
        this.badWordFiltering = badWordFiltering;
    }

    // 1. 메서드에 @BadWordCheck 어노테이션이 붙은 경우 처리
    @Before("@annotation(com.enf.api.component.badword.annotation.BadWordCheck)")
    public void validateAnnotatedMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        BadWordCheck annotation = method.getAnnotation(BadWordCheck.class);

        // 메서드의 모든 파라미터 검사
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof String) {
                checkBadWord(method.getName(), (String) arg, annotation);
            } else if (arg != null) {
                validateObject(arg);
            }
        }
    }

    // 2. 파라미터에 @BadWordCheck 어노테이션이 붙은 경우 처리
    @Before("execution(* *(.., @com.enf.api.component.badword.annotation.BadWordCheck (*), ..))")
    public void validateAnnotatedParameter(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameters.length; i++) {
            BadWordCheck annotation = parameters[i].getAnnotation(BadWordCheck.class);
            if (annotation != null && args[i] != null) {
                if (args[i] instanceof String) {
                    checkBadWord(parameters[i].getName(), (String) args[i], annotation);
                } else {
                    validateObject(args[i]);
                }
            }
        }
    }

    // 객체의 필드 검증
    private void validateObject(Object obj) {
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value instanceof String) {
                    // String 필드만 검사
                    String text = (String) value;
                    // 필드에 @BadWordCheck 어노테이션이 있는 경우
                    BadWordCheck annotation = field.getAnnotation(BadWordCheck.class);
                    if (annotation != null) {
                        checkBadWord(field.getName(), text, annotation);
                    } else {
                        // 어노테이션이 없는 경우에도 검사하려면 (선택적)
                        if (badWordFiltering.check(text)) {
                            BadWordExceptionDTO dto = new BadWordExceptionDTO(field.getName(),text);
                            ResultResponse resultResponse = new ResultResponse(FailedResultType.BAD_WORD_DENIED, dto);
                            throw new GlobalException(resultResponse);
                        }
                    }
                } else if (value != null && !value.getClass().isPrimitive()) {
                    // 중첩 객체의 경우 재귀적으로 검사 (선택적)
                    validateObject(value);
                }
            } catch (IllegalAccessException e) {
                // 예외 처리
            }
        }
    }

    // 비속어 검사 로직
    private void checkBadWord(String name, String text, BadWordCheck annotation) {
        boolean hasBadWord;
        if (annotation.ignoreBlank()) {
            hasBadWord = badWordFiltering.blankCheck(text);
        } else {
            hasBadWord = badWordFiltering.check(text);
        }

        if (hasBadWord) {
            BadWordExceptionDTO dto = new BadWordExceptionDTO(name,text);
            ResultResponse resultResponse = new ResultResponse(FailedResultType.BAD_WORD_DENIED, dto);
            throw new GlobalException(resultResponse);
        }
    }
}