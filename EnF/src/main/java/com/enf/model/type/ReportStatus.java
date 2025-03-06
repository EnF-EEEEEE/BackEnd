package com.enf.model.type;

import lombok.Getter;

// 신고 상태 enum
@Getter
public enum ReportStatus {
    PENDING("대기중"),
    PROCESSING("처리중"),
    COMPLETED("완료됨"),
    REJECTED("거부됨");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

}
