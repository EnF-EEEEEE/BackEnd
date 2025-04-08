package com.enf.api.service;

import com.enf.domain.entity.InquiryEntity;
import com.enf.domain.entity.InquiryResponseEntity;
import com.enf.domain.model.dto.request.inquiry.InquiryDTO;
import com.enf.domain.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface InquiryService {

    /**
     * 문의 등록
     *
     * @param request 요청 객체 (헤더에서 사용자 정보 추출)
     * @param inquiryDTO 문의 내용을 포함한 DTO
     * @return 등록된 문의 정보
     */
    ResultResponse createInquiry(HttpServletRequest request, InquiryDTO inquiryDTO);

    /**
     * 문의 목록 조회 (페이징)
     *
     * @param request 요청 객체 (헤더에서 사용자 정보 추출)
     * @param pageable 페이지 정보
     * @param status 문의 상태 (선택적)
     * @return 문의 목록
     */
    ResultResponse getInquiries(HttpServletRequest request, Pageable pageable, String status);

    /**
     * 문의 상세 조회
     *
     * @param request 요청 객체 (헤더에서 사용자 정보 추출)
     * @param inquirySeq 문의 시퀀스
     * @return 문의 상세 정보
     */
    ResultResponse getInquiryDetail(HttpServletRequest request, Long inquirySeq);

    /**
     * 문의 답변 등록
     *
     * @param request 요청 객체 (헤더에서 사용자 정보 추출)
     * @param inquiryId 문의 ID
     * @param content 답변 내용
     * @return 등록된 답변 정보
     */
    ResultResponse createResponse(HttpServletRequest request, Long inquiryId, String content);
}