package com.enf.api.component.badword;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class BadWordFiltering extends HashSet<String> implements BadWords {
    private String substituteValue = "*";

    // 기본 생성자: 기본 대체 문자는 "*"로 설정하고 비속어 목록을 로드
    public BadWordFiltering() {
        addAll(List.of(koreanWords));
    }

    // 대체 문자를 지정하는 생성자
    public BadWordFiltering(String substituteValue) {
        this();
        this.substituteValue = substituteValue;
    }

    // 비속어가 있다면 대체 문자로 변경
    public String change(String text) {
        String[] words = stream().filter(text::contains).toArray(String[]::new);
        for (String v : words) {
            String sub = this.substituteValue.repeat(v.length());
            text = text.replace(v, sub);
        }
        return text;
    }

    // 비속어를 지정된 문자로 대체 (정규식 패턴 활용)
    public String change(String text, String[] sings) {
        StringBuilder singBuilder = new StringBuilder("[");
        for (String sing : sings) singBuilder.append(Pattern.quote(sing));
        singBuilder.append("]*");
        String patternText = singBuilder.toString();

        for (String word : this) {
            if (word.length() == 1) text = text.replace(word, substituteValue);
            String[] chars = word.chars().mapToObj(Character::toString).toArray(String[]::new);
            text = Pattern.compile(String.join(patternText, chars))
                    .matcher(text)
                    .replaceAll(v -> substituteValue.repeat(v.group().length()));
        }

        return text;
    }

    // 비속어가 1개라도 존재하면 true 반환
    public boolean check(String text) {
        return stream().anyMatch(text::contains);
    }

    // 공백을 제거한 상태에서 비속어를 확인 (예: "시 발" -> "시발" 감지)
    public boolean blankCheck(String text) {
        return check(text.replace(" ", ""));
    }
}
