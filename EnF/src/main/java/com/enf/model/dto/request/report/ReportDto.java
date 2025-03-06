package com.enf.model.dto.request.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ReportDto {

    // 신고 생성 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String category;         // 신고 카테고리 (enum의 description)
        private Long letterSeq;          // 신고 대상 편지 ID
    }

    // 신고 기본 정보 DTO (목록 조회용)
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private Long id;                 // 신고 ID
        private String category;         // 신고 카테고리
        private String targetId;         // 신고 대상 ID (편지_ID 형식)
        private String reporter;         // 신고자 닉네임
        private LocalDateTime createdAt; // 신고 시간
        private String status;           // 상태
    }

    // 신고 상세 정보 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailResponse {
        private Long id;                 // 신고 ID
        private String category;         // 신고 카테고리
        private String targetId;         // 신고 대상 ID (편지_ID 형식)
        private String reporter;         // 신고자 닉네임
        private LocalDateTime createdAt; // 신고 시간
        private LetterInfo letterInfo;   // 신고된 편지 정보
        private String status;           // 상태
        private List<ProcessInfo> process; // 처리 이력
    }

    // 신고된 편지 정보 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LetterInfo {
        private Long letterSeq;          // 편지 ID
        private String letterTitle;      // 편지 제목
        private String letterContent;    // 편지 내용
        private String sender;           // 발신자 닉네임
        private String receiver;         // 수신자 닉네임
        private String categoryName;     // 편지 카테고리
        private LocalDateTime createAt;  // 편지 작성 시간
    }

    // 신고 처리 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessRequest {
        private String status;           // 상태 업데이트
        private String note;             // 처리 내용
        private String action;           // 처리 액션 (옵션)
    }

    // 신고 처리 정보 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessInfo {
        private String status;           // 처리 상태
        private String note;             // 처리 내용
        private String handler;          // 처리자 닉네임
        private LocalDateTime timestamp; // 처리 시간
        private String action;           // 취한 조치 (옵션)
    }

    // 응답 결과 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse {
        private boolean success;         // 성공 여부
        private String message;          // 메시지
        private Object data;             // 추가 데이터 (옵션)
    }
}
