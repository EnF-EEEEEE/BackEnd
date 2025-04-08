package com.enf.api.service.impl;

import com.enf.api.component.facade.UserFacade;
import com.enf.domain.entity.InquiryEntity;
import com.enf.domain.entity.InquiryResponseEntity;
import com.enf.domain.entity.UserEntity;
import com.enf.domain.model.dto.request.inquiry.InquiryDTO;
import com.enf.domain.model.dto.request.inquiry.InquiryDetailDTO;
import com.enf.domain.model.dto.request.inquiry.InquiryPageResponseDTO;
import com.enf.domain.model.dto.response.ResultResponse;
import com.enf.domain.model.dto.response.inquiry.CreateInquiryResponseDTO;
import com.enf.domain.model.dto.response.inquiry.InquiryResponseResultDTO;
import com.enf.domain.model.type.FailedResultType;
import com.enf.domain.model.type.SuccessResultType;
import com.enf.domain.model.type.TokenType;
import com.enf.domain.repository.InquiryRepository;
import com.enf.domain.repository.InquiryResponseRepository;
import com.enf.domain.repository.UserRepository;
import com.enf.api.service.InquiryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryResponseRepository responseRepository;
    private final UserRepository userRepository;
    private final UserFacade userFacade;

    @Override
    @Transactional
    public ResultResponse createInquiry(HttpServletRequest request, InquiryDTO inquiryDTO) {
        UserEntity user = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));

        InquiryEntity inquiry = InquiryEntity.builder()
                .user(user)
                .content(inquiryDTO.getContent())
                .status(InquiryEntity.InquiryStatus.PENDING)
                .createAt(LocalDateTime.now())
                .build();

        // 내용의 처음 10글자를 제목으로 설정
        inquiry.setTitleFromContent();
        InquiryEntity inquiryResult = inquiryRepository.save(inquiry);

        // CreateInquiryResponseDTO 사용하여 응답 생성
        CreateInquiryResponseDTO responseDTO = CreateInquiryResponseDTO.of(inquiryResult.getInquirySeq());
        return new ResultResponse(SuccessResultType.SUCCESS_CREATE_INQUIRY, responseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultResponse getInquiries(HttpServletRequest request, Pageable pageable, String status) {
        UserEntity adminAccount = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
        if (!adminAccount.getRole().getRoleName().equals("ADMIN")) {
            return new ResultResponse(FailedResultType.ADMIN_PERMISSION_DENIED, null);
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
        } else {
            inquiries = inquiryRepository.findAll(pageable);
        }

        // InquiryPageResponseDTO 사용하여 응답 생성
        InquiryPageResponseDTO responseDTO = InquiryPageResponseDTO.from(inquiries);
        return new ResultResponse(SuccessResultType.SUCCESS_GET_INQUIRY, responseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultResponse getInquiryDetail(HttpServletRequest request, Long inquirySeq) {
        UserEntity adminAccount = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
        if (!adminAccount.getRole().getRoleName().equals("ADMIN")) {
            return new ResultResponse(FailedResultType.ADMIN_PERMISSION_DENIED, null);
        }

        try {
            InquiryEntity inquiry = inquiryRepository.findById(inquirySeq)
                    .orElseThrow(() -> new EntityNotFoundException("문의를 찾을 수 없습니다: " + inquirySeq));

            // InquiryDetailDTO 사용하여 응답 생성
            InquiryDetailDTO detailDTO = InquiryDetailDTO.from(inquiry);
            return new ResultResponse(SuccessResultType.SUCCESS_GET_INQUIRY, detailDTO);
        } catch (EntityNotFoundException e) {
            return new ResultResponse(FailedResultType.INQUIRY_NOT_FOUND, null);
        }
    }

    @Override
    @Transactional
    public ResultResponse createResponse(HttpServletRequest request, Long inquiryId, String content) {
        UserEntity adminAccount = userFacade.getUserByToken(request.getHeader(TokenType.ACCESS.getValue()));
        if (!adminAccount.getRole().getRoleName().equals("ADMIN")) {
            return new ResultResponse(FailedResultType.ADMIN_PERMISSION_DENIED, null);
        }

        Long inquirySeq = inquiryId;
        Long adminSeq = adminAccount.getUserSeq();

        try {
            InquiryEntity inquiry = inquiryRepository.findById(inquirySeq)
                    .orElseThrow(() -> new EntityNotFoundException("문의를 찾을 수 없습니다: " + inquirySeq));

            UserEntity admin = userRepository.findById(adminSeq)
                    .orElseThrow(() -> new EntityNotFoundException("관리자를 찾을 수 없습니다: " + adminSeq));

            // 이미 답변이 있는지 확인
            if (inquiry.getStatus() == InquiryEntity.InquiryStatus.ANSWERED) {
                return new ResultResponse(FailedResultType.INQUIRY_ALREADY_ANSWERED, null);
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

            InquiryResponseEntity responseEntity = responseRepository.save(responseInquiry);

            // InquiryResponseResultDTO 사용하여 응답 생성
            InquiryResponseResultDTO resultDTO = InquiryResponseResultDTO.from(responseEntity);

            return new ResultResponse(SuccessResultType.SUCCESS_CREATE_INQUIRY_RESPONSE, resultDTO);
        } catch (EntityNotFoundException e) {
            return new ResultResponse(FailedResultType.INQUIRY_NOT_FOUND, null);
        }
    }
}