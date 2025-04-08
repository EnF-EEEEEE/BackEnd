package com.enf.api.controller;

import com.enf.domain.model.dto.request.inquiry.InquiryDTO;
import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.domain.model.dto.response.letter.LetterDetailResponseDto;
import com.enf.api.service.AdminService;
import com.enf.api.service.AuthService;
import com.enf.api.service.InquiryService;
import com.enf.api.service.LetterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;
    private final InquiryService inquiryService;
    private final LetterService letterService;

    /**
     * 카카오 로그인 후 리다이렉트 URL로 코드값을 처리해서 다음 화면으로 넘겨줌
     * @param request
     * @param response
     * @param code : 카카오톡에서 받아오는 code값
     * @param model
     * @return
     *  /admin/dashboard -> dashboard.html로 이동시켜줌
     */
    @GetMapping("/callback")
    public String kakaoCallback(HttpServletRequest request,
                                HttpServletResponse response,
                                @RequestParam("code") String code, Model model) {
        authService.oAuthForKakao(request,response, code);
        model.addAttribute("access", response.getHeader("access"));
        // 수정된 부분: 슬래시 없이 템플릿 이름만 지정
        return "admin/dashboard";  // 페이지 이동
    }

    /**
     * 대시보드 접근시 최초 데이터 리스폰
     * @return
     *
     */
    @GetMapping("/dashboard-data")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        return adminService.getDashboardData();
    }


    //----------문의 기능----------

    /**
     * 문의 목록 조회 API
     *
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @param status 문의 상태 (선택적: PENDING, ANSWERED)
     * @return 문의 목록 정보
     */
    @GetMapping("/inquiries")
    public ResponseEntity<ResultResponse> getInquiries(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {

        // 페이지 정보 구성 (최신순 정렬)
        Pageable pageable = PageRequest.of(page, size, Sort.by("createAt").descending());
        // 응답 반환
        ResultResponse response =  inquiryService.getInquiries(request, pageable, status);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * 문의 상세 조회 API
     *
     * @param id 문의 시퀀스
     * @return 문의 상세 정보
     */
    @GetMapping("/inquiries/{id}")
    public ResponseEntity<ResultResponse> getInquiryDetail(
            HttpServletRequest request,
            @PathVariable Long id) {
        ResultResponse response =  inquiryService.getInquiryDetail(request, id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    /**
     * 문의 답변 등록 API
     *
     * @param id 문의 시퀀스
     * @param inquiryDTO 답변 내용
     * @param request 요청 객체 (헤더에서 관리자 정보 추출)
     * @return 등록 결과
     */
    @PostMapping("/inquiries/{id}/responses")
    public ResponseEntity<ResultResponse> createResponse(
            @PathVariable Long id,
            @RequestBody InquiryDTO inquiryDTO,
            HttpServletRequest request) {

        String content = inquiryDTO.getContent();
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ResultResponse response =  inquiryService.createResponse(request, id, content);
        return new ResponseEntity<>(response, response.getStatus());
    }

    //-----------편지관련 api-----------

    /**
     * 모든 편지 목록 조회 API - 최신순 페이징만 적용
     */
    @GetMapping("/letters")
    public ResponseEntity<Map<String, Object>> getLetters(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("멘티 편지 목록 요청 - 페이지: {}, 사이즈: {}", page, size);
        return letterService.getAllMenteeLetters(request,page, size);
    }

    /**
     * 편지 상세 조회 API
     */
    @GetMapping("/letters/{id}")
    public ResponseEntity<LetterDetailResponseDto> getLetterDetail(
            HttpServletRequest request, @PathVariable Long id) {

        return letterService.getLetterContent(request, id);
    }

}
