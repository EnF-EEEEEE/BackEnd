package com.enf.api.service;

import com.enf.domain.model.dto.request.report.ReportDto;
import com.enf.domain.model.type.ReportCategory;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportService {

    /**
     * 신고 생성
     *
     * @param request 신고 생성 요청 DTO
     * @param userSeq 신고자 ID
     * @return 생성된 신고의 ID
     */
    Long createReport(ReportDto.CreateRequest request, Long userSeq);

    /**
     * 신고 목록 조회
     *
     * @param status 조회할 상태 (ALL인 경우 모든 상태)
     * @param search 검색어 (null인 경우 검색 조건 없음)
     * @param pageable 페이징 정보
     * @return 신고 목록
     */
    Page<ReportDto.ListResponse> getReports(String status, String search, Pageable pageable);

    /**
     * 신고 상세 정보 조회
     *
     * @param reportId 신고 ID
     * @return 신고 상세 정보
     */
    ReportDto.DetailResponse getReportDetail(Long reportId);

    /**
     * 신고 처리 이력 조회
     *
     * @param reportId 신고 ID
     * @return 처리 이력 목록
     */
    List<ReportDto.ProcessInfo> getReportProcessHistory(Long reportId);

    /**
     * 신고 처리 상태 업데이트
     *
     * @param reportId 신고 ID
     * @param request 처리 요청 DTO
     * @param handlerSeq 처리자 ID
     * @return 처리 결과
     */
    ReportDto.ApiResponse updateReportProcess(Long reportId, ReportDto.ProcessRequest request, Long handlerSeq);

    /**
     * 미처리 신고 수 조회
     *
     * @return 미처리 신고 수
     */
    Long countPendingReports();

    /**
     * 카테고리별 신고 수 통계 조회
     *
     * @return 카테고리별 신고 수
     */
    List<CategoryStatistics> getCategoryStatistics();

    /**
     * 카테고리별 신고 통계
     */
    @Getter
    @AllArgsConstructor
    class CategoryStatistics {
        private ReportCategory category;
        private String categoryName;
        private Long count;
    }
}
