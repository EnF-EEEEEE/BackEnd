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
import java.util.HashSet;
import java.util.Set;

@Aspect
@Component
public class BadWordAspect {

    private final BadWordFiltering badWordFiltering;

    // 방문한 객체 추적을 위한 Set 추가
    private final Set<Object> visitedObjects = new HashSet<>();
    // 최대 재귀 깊이 제한
    private static final int MAX_DEPTH = 5;

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

    // 객체의 필드 검증 (depth 매개변수 추가)
    private void validateObject(Object obj, int depth) {
        // 최대 깊이 초과 또는 이미 방문한 객체는 검사하지 않음
        if (depth > MAX_DEPTH || obj == null || visitedObjects.contains(obj)) {
            return;
        }

        // 방문 객체 추가
        visitedObjects.add(obj);

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
                            BadWordExceptionDTO dto = new BadWordExceptionDTO(field.getName(), text);
                            ResultResponse resultResponse = new ResultResponse(FailedResultType.BAD_WORD_DENIED, dto);
                            throw new GlobalException(resultResponse);
                        }
                    }
                } else if (value != null && !value.getClass().isPrimitive() &&
                        !value.getClass().getName().startsWith("java.")) {
                    // 중첩 객체의 경우 재귀적으로 검사 (Java 표준 라이브러리 객체는 제외)
                    validateObject(value, depth + 1);
                }
            } catch (IllegalAccessException e) {
                // 예외 처리
            }
        }
    }

    // 기존 메서드 오버로드
    private void validateObject(Object obj) {
        visitedObjects.clear(); // 새로운 검증 시작 전 방문 기록 초기화
        validateObject(obj, 0);
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