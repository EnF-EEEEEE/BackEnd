package com.enf.service.impl;

import com.enf.entity.UserEntity;
import com.enf.repository.LetterStatusRepository;
import com.enf.repository.UserRepository;
import com.enf.service.ChurnAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;


/**
 * 이탈율 분석을 위한 서비스 클래스
 */
@Service
public class ChurnAnalysisServiceImpl implements ChurnAnalysisService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LetterStatusRepository letterStatusRepository;

    /**
     * 회원 가입일로부터 24시간 이내에 편지를 작성한 멘티의 수를 주 단위로 조회
     * @param startDate 1주차 시작 날짜
     * @param weekCount 조회할 주 수 (기본 7주)
     * @return 각 주차별 24시간 이내 편지 작성 멘티 수
     */
    public List<Integer> getMenteesWithLetterWithin24Hours(LocalDate startDate, int weekCount) {
        // 결과를 저장할 리스트
        List<Integer> weeklyMenteeCounts = new ArrayList<>();

        // 시작 날짜가 월요일이 아니면 그 주의 월요일로 조정
        LocalDate adjustedStartDate = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 각 주차별 데이터 조회
        for (int weekIndex = 0; weekIndex < weekCount; weekIndex++) {
            // 현재 주차의 시작일과 종료일 계산
            LocalDate weekStartDate = adjustedStartDate.plusWeeks(weekIndex);
            LocalDate weekEndDate = weekStartDate.plusDays(6); // 일요일

            // 해당 주간에 가입한 멘티 중 24시간 이내에 편지를 작성한 수 조회
            int count = countMenteesWithLetterWithin24Hours(weekStartDate, weekEndDate);
            weeklyMenteeCounts.add(count);
        }

        return weeklyMenteeCounts;
    }

    /**
     * 특정 기간에 가입한 멘티 중 가입 후 24시간 이내에 편지를 작성한 멘티 수 조회
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 가입 후 24시간 이내 편지 작성 멘티 수
     */
    public int countMenteesWithLetterWithin24Hours(LocalDate startDate, LocalDate endDate) {
        // 시작일과 종료일을 LocalDateTime으로 변환 (시작일은 00:00:00, 종료일은 23:59:59.999999999)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // 해당 기간에 가입한 멘티 목록 조회 (role_seq = 5는 MENTEE)
        List<UserEntity> newMentees = userRepository.findByRoleRoleSeqAndCreateAtBetween(5L, startDateTime, endDateTime);

        int count = 0;

        // 각 멘티에 대해 가입 후 24시간 이내 편지 작성 여부 확인
        for (UserEntity mentee : newMentees) {
            LocalDateTime registrationTime = mentee.getCreateAt();
            LocalDateTime cutoffTime = registrationTime.plusHours(24);

            // 멘티가 작성한 편지 중 가입 후 24시간 이내에 작성된 것이 있는지 확인
            boolean hasLetterWithin24Hours = letterStatusRepository.existsByMenteeAndMenteeLetterIsNotNullAndCreateAtBetween(
                    mentee, registrationTime, cutoffTime);

            if (hasLetterWithin24Hours) {
                count++;
            }
        }

        return count;
    }

    /**
     * 1월 20일부터 시작하는 7주간의 데이터 조회 (기본 메서드)
     * @return 각 주차별 24시간 이내 편지 작성 멘티 수
     */
    public List<Integer> getDefaultWeeklyMenteeCounts() {
        LocalDate startDate = LocalDate.of(2025, 1, 20); // 2025년 1월 20일 시작
        return getMenteesWithLetterWithin24Hours(startDate, 7);
    }
}
