package com.enf.api.controller;

import com.enf.api.component.badword.annotation.BadWordCheck;
import com.enf.api.service.InquiryService;
import com.enf.domain.model.dto.request.inquiry.InquiryDTO;
import com.enf.domain.model.dto.response.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    /**
     * 문의 등록 API
     *
     * @param request 요청 객체 (헤더에서 사용자 정보 추출)
     * @param inquiryDTO 문의 내용을 포함한 DTO
     * @return 등록 결과
     */
    @PostMapping
    public ResponseEntity<ResultResponse> createInquiry(
            HttpServletRequest request,
            @RequestBody
            @BadWordCheck
            InquiryDTO inquiryDTO) {

        // 문의 내용 검증
        if (inquiryDTO.getContent() == null || inquiryDTO.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ResultResponse response = inquiryService.createInquiry(request, inquiryDTO);
        return new ResponseEntity<>(response, response.getStatus());
    }
}