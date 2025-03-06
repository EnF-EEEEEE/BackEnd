package com.enf.model.type;

import lombok.Getter;

/**
 * 신고 카테고리 Enum
 */
@Getter
public enum ReportCategory {
    EMERGENCY("위급한 상황이라고 판단"),
    IRRELEVANT("주제와 맞지 않는 내용"),
    ABUSIVE("욕설 / 비하발언 / 특정인 비방"),
    SOLICITATION("만남 유도나 금전 거래"),
    OBSCENE("외설적 / 음란성 / 성희롱적 표현"),
    ADVERTISEMENT("광고성 / 홍보성"),
    OTHER("기타");

    private final String description;

    ReportCategory(String description) {
        this.description = description;
    }

    /**
     * 설명을 기반으로 카테고리 Enum 반환
     *
     * @param description 카테고리 설명
     * @return 해당 설명과 일치하는 카테고리 Enum, 일치하는 항목이 없으면 OTHER 반환
     */
    public static ReportCategory fromDescription(String description) {
        for (ReportCategory category : ReportCategory.values()) {
            if (category.getDescription().equals(description)) {
                return category;
            }
        }
        return OTHER; // 기본값으로 기타 반환
    }
}
