package com.enf.service.impl;

import com.enf.component.facade.UserFacade;
import com.enf.entity.InquiryEntity;
import com.enf.entity.InquiryResponseEntity;
import com.enf.entity.UserEntity;
import com.enf.model.type.TokenType;
import com.enf.repository.InquiryRepository;
import com.enf.repository.InquiryResponseRepository;
import com.enf.repository.UserRepository;
import com.enf.service.InquiryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryResponseRepository responseRepository;
    private final UserRepository userRepository;
    private final UserFacade userFacade;

    @Override
    @Transactional
    public ResponseEntity<Map<String, Object>> createInquiry(HttpServletRequest request, String content) {

        UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

        InquiryEntity inquiry = InquiryEntity.builder()
                .user(user)
                .content(content)
                .status(InquiryEntity.InquiryStatus.PENDING)
                .createAt(LocalDateTime.now())
                .build();

        // 내용의 처음 10글자를 제목으로 설정
        inquiry.setTitleFromContent();
        InquiryEntity inquiryResult = inquiryRepository.save(inquiry);

        // 응답 구성
        Map<String, Object> response = new HashMap<>();
        response.put("message", "문의가 성공적으로 등록되었습니다.");
        response.put("inquiryId", inquiryResult.getInquirySeq());

        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getInquiries(HttpServletRequest request, Pageable pageable, String status) {

        UserEntity adminAccount = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
        if (!adminAccount.getRole().getRoleName().equals("ADMIN")) {
            return ResponseEntity.badRequest().build();
        }

        Page<InquiryEntity> inquiries;

        if (status != null && !status.isEmpty()) {
            try {
                InquiryEntity.InquiryStatus inquiryStatus = InquiryEntity.InquiryStatus.valueOf(status);
                inquiries = inquiryRepository.findByStatus(inquiryStatus, pageable);
            } catch (IllegalArgumentException e) {
                // 잘못된 상태값이 전달된 경우 모든 문의 반환
                inquiries = inquiryRepository.findAll(pageable);
            }
        }
        inquiries = inquiryRepository.findAll(pageable);

        return ResponseEntity.ok(convertToResponse(inquiries, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getInquiryDetail(HttpServletRequest request,Long inquirySeq) {

        UserEntity adminAccount = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
        if (!adminAccount.getRole().getRoleName().equals("ADMIN")) {
            return ResponseEntity.badRequest().build();
        }

        InquiryEntity inquiry = inquiryRepository.findById(inquirySeq)
                .orElseThrow(() -> new EntityNotFoundException("문의를 찾을 수 없습니다: " + inquirySeq));
        return ResponseEntity.ok(convertToDetailResponse(inquiry));
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, Object>> createResponse(HttpServletRequest request, Long adminSeq, String content) {

        UserEntity adminAccount = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
        if (!adminAccount.getRole().getRoleName().equals("ADMIN")) {
            return ResponseEntity.badRequest().build();
        }
        Long inquirySeq = adminAccount.getUserSeq();

        InquiryEntity inquiry = inquiryRepository.findById(inquirySeq)
                .orElseThrow(() -> new EntityNotFoundException("문의를 찾을 수 없습니다: " + inquirySeq));

        UserEntity admin = userRepository.findById(adminSeq)
                .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다: " + adminSeq));

        // 이미 답변이 있는지 확인
        if (inquiry.getStatus() == InquiryEntity.InquiryStatus.ANSWERED) {
            throw new IllegalStateException("이미 답변이 등록된 문의입니다.");
        }

        InquiryResponseEntity responseInquiry = InquiryResponseEntity.builder()
                .inquiry(inquiry)
                .content(content)
                .admin(admin)
                .createAt(LocalDateTime.now())
                .build();

        // 문의 상태 변경
        inquiry.markAsAnswered();
        inquiryRepository.save(inquiry);

        InquiryResponseEntity response = responseRepository.save(responseInquiry);
        // 응답 구성
        Map<String, Object> result = new HashMap<>();
        result.put("message", "답변이 성공적으로 등록되었습니다.");
        result.put("response", convertToResponseDetail(response));

        return ResponseEntity.ok(result);
    }

    @Override
    public Map<String, Object> convertToResponse(Page<InquiryEntity> inquiries, Pageable pageable) {
        Map<String, Object> response = new HashMap<>();

        response.put("inquiries", inquiries.getContent().stream()
                .map(inquiry -> {
                    Map<String, Object> inquiryMap = new HashMap<>();
                    inquiryMap.put("id", inquiry.getInquirySeq());
                    inquiryMap.put("title", inquiry.getTitle());
                    inquiryMap.put("author", inquiry.getUser().getNickname());
                    inquiryMap.put("createdAt", inquiry.getCreateAt().toString());
                    inquiryMap.put("status", inquiry.getStatus().name());
                    return inquiryMap;
                })
                .collect(Collectors.toList()));

        response.put("currentPage", pageable.getPageNumber());
        response.put("totalItems", inquiries.getTotalElements());
        response.put("totalPages", inquiries.getTotalPages());

        return response;
    }

    @Override
    public Map<String, Object> convertToDetailResponse(InquiryEntity inquiry) {
        Map<String, Object> response = new HashMap<>();

        response.put("id", inquiry.getInquirySeq());
        response.put("title", inquiry.getTitle());
        response.put("content", inquiry.getContent());
        response.put("author", inquiry.getUser().getNickname());
        response.put("createdAt", inquiry.getCreateAt().toString());
        response.put("status", inquiry.getStatus().name());

        // 답변이 있는 경우 응답에 포함
        if (inquiry.getStatus() == InquiryEntity.InquiryStatus.ANSWERED && inquiry.getResponse() != null) {
            response.put("response", convertToResponseDetail(inquiry.getResponse()));
        }

        return response;
    }

    @Override
    public Map<String, Object> convertToResponseDetail(InquiryResponseEntity response) {
        Map<String, Object> responseMap = new HashMap<>();

        responseMap.put("id", response.getResponseSeq());
        responseMap.put("content", response.getContent());
        responseMap.put("respondent", response.getAdmin().getNickname());
        responseMap.put("createdAt", response.getCreateAt().toString());

        return responseMap;
    }
}