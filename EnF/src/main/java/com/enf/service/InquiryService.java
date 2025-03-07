package com.enf.service;

import com.enf.entity.InquiryEntity;
import com.enf.entity.InquiryResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface InquiryService {

    /**
     * 문의 등록
     *
     * @param content 문의 내용
     * @return 등록된 문의 정보
     */
    ResponseEntity<Map<String, Object>> createInquiry(HttpServletRequest request, String content);

    /**
     * 문의 목록 조회 (페이징)
     *
     * @param pageable 페이지 정보
     * @param status 문의 상태 (선택적)
     * @return 문의 목록
     */
    ResponseEntity<Map<String, Object>> getInquiries(HttpServletRequest request, Pageable pageable, String status);

    /**
     * 문의 상세 조회
     *
     * @param inquirySeq 문의 시퀀스
     * @return 문의 상세 정보
     */
    ResponseEntity<Map<String, Object>> getInquiryDetail(HttpServletRequest request, Long inquirySeq);

    /**
     * 문의 답변 등록
     *
     * @param adminSeq 관리자 시퀀스
     * @param content 답변 내용
     * @return 등록된 답변 정보
     */
    ResponseEntity<Map<String, Object>> createResponse(HttpServletRequest request, Long adminSeq, String content);

    /**
     * 문의 목록을 응답 형식으로 변환
     *
     * @param inquiries 문의 목록
     * @param pageable 페이지 정보
     * @return 응답 데이터
     */
    Map<String, Object> convertToResponse(Page<InquiryEntity> inquiries, Pageable pageable);

    /**
     * 문의 상세 정보를 응답 형식으로 변환
     *
     * @param inquiry 문의 상세 정보
     * @return 응답 데이터
     */
    Map<String, Object> convertToDetailResponse(InquiryEntity inquiry);

    /**
     * 답변 정보를 응답 형식으로 변환
     *
     * @param response 답변 정보
     * @return 응답 데이터
     */
    Map<String, Object> convertToResponseDetail(InquiryResponseEntity response);
}