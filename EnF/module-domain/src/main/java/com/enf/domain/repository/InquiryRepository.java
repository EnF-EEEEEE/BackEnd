package com.enf.domain.repository;

import com.enf.domain.entity.InquiryEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<InquiryEntity, Long> {

    // 상태별 문의 조회 (페이징)
    Page<InquiryEntity> findByStatus(InquiryEntity.InquiryStatus status, Pageable pageable);

    // 특정 사용자의 문의 조회
    Page<InquiryEntity> findByUserUserSeq(Long userSeq, Pageable pageable);

    // ID로 문의 조회 (조인 쿼리 최적화)
    @Query("SELECT i FROM inquiry i LEFT JOIN FETCH i.response LEFT JOIN FETCH i.user WHERE i.inquirySeq = :inquirySeq")
    Optional<InquiryEntity> findByIdWithDetails(@Param("inquirySeq") Long inquirySeq);
}