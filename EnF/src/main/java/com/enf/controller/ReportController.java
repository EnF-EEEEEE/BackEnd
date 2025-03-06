package com.enf.controller;


import com.enf.component.facade.UserFacade;
import com.enf.model.dto.request.report.ReportDto;
import com.enf.model.type.TokenType;
import com.enf.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final UserFacade userFacade;

    /**
     * 신고하기 API
     */
    @PostMapping("/api/v1/reports")
    public ResponseEntity<Map<String, Object>> createReport(@RequestBody ReportDto.CreateRequest dtoRequest, HttpServletRequest request) {

        // userSeq조회
        Long userSeq = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue())).getUserSeq();

        // 신고 생성
        Long reportId = reportService.createReport(dtoRequest, userSeq);

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("reportId", reportId);
        response.put("message", "신고가 접수되었습니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * 신고 목록 조회 API (관리자용)
     */
    @GetMapping("/api/v1/admin/reports")
    public ResponseEntity<Map<String, Object>> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false) String search) {

        // 페이징 정보 생성 (생성일 내림차순)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());

        // 신고 목록 조회
        Page<ReportDto.ListResponse> reports = reportService.getReports(status, search, pageable);

        // 미처리 신고 수 조회
        Long pendingCount = reportService.countPendingReports();

        // 응답 데이터 구조 생성
        Map<String, Object> response = new HashMap<>();
        response.put("reports", reports.getContent());
        response.put("currentPage", reports.getNumber());
        response.put("totalPages", reports.getTotalPages());
        response.put("totalItems", reports.getTotalElements());
        response.put("pendingCount", pendingCount);

        return ResponseEntity.ok(response);
    }

    /**
     * 신고 상세 조회 API (관리자용)
     */
    @GetMapping("/api/v1/admin/reports/{id}")
    public ResponseEntity<Map<String, Object>> getReportDetail(@PathVariable Long id) {
        // 신고 상세 정보 조회
        ReportDto.DetailResponse reportDetail = reportService.getReportDetail(id);

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("id", reportDetail.getId());
        response.put("category", reportDetail.getCategory());
        response.put("targetId", reportDetail.getTargetId());
        response.put("reporter", reportDetail.getReporter());
        response.put("createdAt", reportDetail.getCreatedAt());
        response.put("letterInfo", reportDetail.getLetterInfo());
        response.put("status", reportDetail.getStatus());
        response.put("process", reportDetail.getProcess());

        return ResponseEntity.ok(response);
    }

    /**
     * 신고 처리 상태 업데이트 API (관리자용)
     */
    @PostMapping("/api/v1/admin/reports/{id}/process")
    public ResponseEntity<Map<String, Object>> updateReportProcess(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody ReportDto.ProcessRequest dtoRequest) {


        // userSeq조회
        Long userSeq = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue())).getUserSeq();


        // 신고 처리 상태 업데이트
        ReportDto.ApiResponse result = reportService.updateReportProcess(id, dtoRequest, userSeq);

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());

        return result.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    /**
     * 카테고리별 신고 통계 조회 API (관리자용)
     */
    @GetMapping("/api/v1/admin/reports/statistics/category")
    public ResponseEntity<Map<String, Object>> getCategoryStatistics() {
        // 카테고리별 통계 조회
        List<ReportService.CategoryStatistics> statistics = reportService.getCategoryStatistics();

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();

        // 카테고리별 데이터
        List<Map<String, Object>> categoriesData = statistics.stream()
                .map(stat -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("category", stat.getCategory().name());
                    data.put("description", stat.getCategoryName());
                    data.put("count", stat.getCount());
                    return data;
                })
                .collect(java.util.stream.Collectors.toList());

        response.put("categories", categoriesData);
        response.put("totalReports", statistics.stream().mapToLong(ReportService.CategoryStatistics::getCount).sum());

        return ResponseEntity.ok(response);
    }
}