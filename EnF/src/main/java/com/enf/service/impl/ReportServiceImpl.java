package com.enf.service.impl;

import com.enf.component.facade.UserFacade;
import com.enf.entity.*;
import com.enf.exception.GlobalException;
import com.enf.exception.GlobalExceptionHandler;
import com.enf.model.dto.request.report.ReportDto;
import com.enf.model.type.FailedResultType;
import com.enf.model.type.PenaltyType;
import com.enf.model.type.ReportCategory;
import com.enf.model.type.ReportStatus;
import com.enf.repository.*;
import com.enf.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ReportProcessRepository reportProcessRepository;
    private final UserRepository userRepository;
    private final LetterRepository letterRepository;
    private final PenaltyRepository penaltyRepository;
    private final LetterStatusRepository letterStatusRepository;

    @Override
    @Transactional
    public Long createReport(ReportDto.CreateRequest request, Long userSeq) {
        // 신고자 정보 조회
        UserEntity reporter = userRepository.findById(userSeq)
                .orElseThrow(() -> new NoSuchElementException("해당하는 사용자를 찾을 수 없습니다."));

        // 신고 대상 편지 조회
        LetterEntity letter = letterRepository.findById(request.getLetterSeq())
                .orElseThrow(() -> new NoSuchElementException("해당하는 편지를 찾을 수 없습니다."));

        // 카테고리 변환
        ReportCategory category = ReportCategory.fromDescription(request.getCategory());

        // 신고 엔티티 생성
        ReportEntity reportEntity = ReportEntity.builder()
                .category(category)
                .letter(letter)
                .reporter(reporter)
                .status(ReportStatus.PENDING)
                .createAt(LocalDateTime.now())
                .build();

        // 저장 및 ID 반환
        return reportRepository.save(reportEntity).getReportSeq();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportDto.ListResponse> getReports(String status, String search, Pageable pageable) {
        // 검색 조건에 따른 신고 목록 조회
        Page<ReportEntity> reports;

        if (search == null || search.trim().isEmpty()) {
            if ("ALL".equals(status)) {
                reports = reportRepository.findAll(pageable);
            } else {
                try {
                    ReportStatus statusEnum = ReportStatus.valueOf(status);
                    reports = reportRepository.findByStatus(statusEnum, pageable);
                } catch (IllegalArgumentException e) {
                    reports = reportRepository.findAll(pageable);
                }
            }
        } else {
            reports = reportRepository.findBySearchCriteria(status, search, pageable);
        }

        // DTO 변환
        return reports.map(report -> ReportDto.ListResponse.builder()
                .id(report.getReportSeq())
                .category(report.getCategory().getDescription())
                .targetId("letter_" + report.getLetter().getLetterSeq())
                .reporter(report.getReporter().getNickname())
                .createdAt(report.getCreateAt())
                .status(report.getStatus().name())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto.DetailResponse getReportDetail(Long reportId) {
        // 신고 상세 정보 조회
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 신고를 찾을 수 없습니다."));

        // 편지 정보 조회
        LetterEntity letter = report.getLetter();

        // 처리 이력 조회
        List<ReportDto.ProcessInfo> processHistory = getReportProcessHistory(reportId);

        // 편지 정보 DTO 변환
        ReportDto.LetterInfo letterInfo = ReportDto.LetterInfo.builder()
                .letterSeq(letter.getLetterSeq())
                .letterTitle(letter.getLetterTitle())
                .letterContent(letter.getLetter())
                .categoryName(letter.getCategoryName())
                .createAt(letter.getCreateAt())
                .build();

        // 발신자/수신자 정보가 있을 경우 추가
        // 실제 구현에서는 LetterStatusEntity를 통해 발신자/수신자 정보를 조회할 수 있음

        // DTO 변환
        return ReportDto.DetailResponse.builder()
                .id(report.getReportSeq())
                .category(report.getCategory().getDescription())
                .targetId("letter_" + letter.getLetterSeq())
                .reporter(report.getReporter().getNickname())
                .createdAt(report.getCreateAt())
                .letterInfo(letterInfo)
                .status(report.getStatus().name())
                .process(processHistory)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto.ProcessInfo> getReportProcessHistory(Long reportId) {
        // 신고 조회
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("해당하는 신고를 찾을 수 없습니다."));

        // 처리 이력 조회
        List<ReportProcessEntity> processes = reportProcessRepository.findByReportOrderByCreateAtAsc(report);

        // DTO 변환
        return processes.stream()
                .map(process -> ReportDto.ProcessInfo.builder()
                        .status(process.getStatus().name())
                        .note(process.getNote())
                        .handler(process.getHandler().getNickname())
                        .timestamp(process.getCreateAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReportDto.ApiResponse updateReportProcess(Long reportId, ReportDto.ProcessRequest request, Long handlerSeq) {
        // 처리 내용 유효성 검사
        if (request.getNote() == null || request.getNote().trim().isEmpty()) {
            return ReportDto.ApiResponse.builder()
                    .success(false)
                    .message("처리 내용을 입력해주세요.")
                    .build();
        }

        try {
            // 신고 조회
            ReportEntity report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new NoSuchElementException("해당하는 신고를 찾을 수 없습니다."));

            // 처리자 조회
            UserEntity handler = userRepository.findById(handlerSeq)
                    .orElseThrow(() -> new NoSuchElementException("해당하는 사용자를 찾을 수 없습니다."));

            // 상태 변환
            ReportStatus newStatus;
            try {
                newStatus = ReportStatus.valueOf(request.getStatus());
            } catch (IllegalArgumentException e) {
                newStatus = ReportStatus.PROCESSING;
            }
            log.info("newStatus : {}", newStatus);
            // 신고 처리 이력 생성
            ReportProcessEntity process = ReportProcessEntity.builder()
                    .report(report)
                    .status(newStatus)
                    .note(request.getNote())
                    .handler(handler)
                    .createAt(LocalDateTime.now())
                    .build();

            // 신고 상태 업데이트
            report = ReportEntity.builder()
                    .reportSeq(report.getReportSeq())
                    .category(report.getCategory())
                    .letter(report.getLetter())
                    .reporter(report.getReporter())
                    .status(newStatus)
                    .createAt(report.getCreateAt())
                    .build();

//            // 저장
//            reportRepository.save(report);
//            reportProcessRepository.save(process);

            // 패널티 처리 로직
            if (newStatus == ReportStatus.PENALTY) {
                //패널티 받을 편지 번호
                Long penaltyLetterSeq = report.getLetter().getLetterSeq();
                //패널티 받을 편지 번호
                LetterStatusEntity penaltyLetterStatus = letterStatusRepository.findByLetterStatusSeq(penaltyLetterSeq);

                // 제재 대상이 해당 편지의 멘토인지 멘티인지 확인
                UserEntity penaltyUser;
                if (penaltyLetterStatus.getMentor().equals(report.getReporter())) {
                    penaltyUser = penaltyLetterStatus.getMentee();
                } else {
                    penaltyUser = penaltyLetterStatus.getMentor();
                }

                PenaltyType penaltyType = PenaltyType.FIRST;
                Optional<PenaltyEntity> latestPenalty = penaltyRepository.findTopByUserOrderByPenaltyAtDesc(penaltyUser);

                if (latestPenalty.isPresent()) {
                    // 제재가 있을경우
                    int penaltyCount = latestPenalty.get().getPenaltyType().getPenaltyCount();
                    log.info("penaltyCount : {}", penaltyCount);
                    switch (penaltyCount) {
                        case 1:
                            penaltyType = PenaltyType.SECOND;
                            break;
                        case 2:
                            penaltyType = PenaltyType.THIRD;
                            break;
                        case 3:
                            penaltyType = PenaltyType.FOURTH;
                            break;
                        case 4:
                            throw new GlobalException(FailedResultType.ALREADY_PENALTY);
                    }
                }

                PenaltyEntity penalty = PenaltyEntity.builder()
                        .user(penaltyUser)
                        .penaltyType(penaltyType)
                        .penaltyAt(LocalDateTime.now())
                        .build();

                penaltyRepository.save(penalty);
            }
            // 저장
            reportRepository.save(report);
            reportProcessRepository.save(process);

            return ReportDto.ApiResponse.builder()
                    .success(true)
                    .message("신고 처리 상태가 업데이트되었습니다.")
                    .build();

        } catch (Exception e) {
            return ReportDto.ApiResponse.builder()
                    .success(false)
                    .message("처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long countPendingReports() {
        return reportRepository.countByStatus(ReportStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryStatistics> getCategoryStatistics() {
        List<CategoryStatistics> statistics = new ArrayList<>();

        // 각 카테고리별 신고 수 조회
        for (ReportCategory category : ReportCategory.values()) {
            Long count = reportRepository.count(
                    (root, query, cb) -> cb.equal(root.get("category"), category)
            );

            statistics.add(new CategoryStatistics(category, category.getDescription(), count));
        }

        return statistics;
    }
}