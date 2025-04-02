package com.enf.api.controller;

import com.enf.api.service.InquiryService;
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
     * @param inquiryRequest 문의 내용
     * @return 등록 결과
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createInquiry(
            HttpServletRequest request,
            @RequestBody Map<String, String> inquiryRequest) {

        // 문의 내용 추출
        String content = inquiryRequest.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return inquiryService.createInquiry(request, content);
    }
}