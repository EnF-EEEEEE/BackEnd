package com.enf.repository;

import com.enf.entity.ReportEntity;
import com.enf.entity.ReportProcessEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 신고 처리에 대한 내용
 */
@Repository
public interface ReportProcessRepository extends JpaRepository<ReportProcessEntity, Long> {

    //신고에 대한 처리 이력 조회
    List<ReportProcessEntity> findByReportOrderByCreateAtAsc(ReportEntity reportEntity);
}
