package com.enf.repository;

import com.enf.entity.LetterEntity;
import com.enf.entity.ReportEntity;
import com.enf.model.type.ReportCategory;
import com.enf.model.type.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> , JpaSpecificationExecutor<ReportEntity> {

    // 상태별 신고 목록 조회
    Page<ReportEntity> findByStatus(ReportStatus status, Pageable pageable);

    // 카테고리별 신고 목록 조회
    Page<ReportEntity> findByCategory(ReportCategory category, Pageable pageable);

    // 검색어로 신고 목록 조회
    @Query("SELECT r FROM report r WHERE " +
            "(:status = 'ALL' OR r.status = :status) AND " +
            "(:search IS NULL OR " +
            "r.category = :search OR " +
            "r.reporter.nickname LIKE %:search%)")
    Page<ReportEntity> findBySearchCriteria(
            @Param("status") String status,
            @Param("search") String search,
            Pageable pageable);

    // 특정 편지에 대한 신고 목록 조회
    List<ReportEntity> findByLetter(LetterEntity letter);

    // 신고 ID로 상세 정보 조회
    Optional<ReportEntity> findByReportSeq(Long reportSeq);

    // 특정 사용자의 신고 목록 조회
    Page<ReportEntity> findByReporterUserSeq(Long userSeq, Pageable pageable);

    // 미처리된 신고 수 조회
    Long countByStatus(ReportStatus status);
}