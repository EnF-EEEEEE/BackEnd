package com.enf.service.impl;

import com.enf.component.facade.UserFacade;
import com.enf.repository.LetterRepository;
import com.enf.repository.querydsl.LetterAppreciationQueryRepository;
import com.enf.repository.querydsl.LetterQueryStatusRepository;
import com.enf.repository.querydsl.UserQueryRepository;
import com.enf.service.AdminService;
import com.enf.service.ChurnAnalysisService;
import com.enf.service.UserConversionService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserFacade userFacade;
    private final UserQueryRepository userQueryRepository;
    private final LetterRepository letterRepository;
    private final LetterQueryStatusRepository letterQueryStatusRepository;
    private final UserConversionService userConversionService;
    private final LetterAppreciationQueryRepository letterAppreciationQueryRepository;
    private final ChurnAnalysisService churnAnalysisService;

    public ResponseEntity<Map<String, Object>> getDashboardData() {

        // 데이터를 담을 맵 생성
        Map<String, Object> dashboardData = new HashMap<>();

        //-------요약 정보 --------
        // 1. 총 사용자 수
        long totalUserCount = userFacade.getTotalUserCount();
        dashboardData.put("totalUsers", totalUserCount);

        // 2. 주간 활성 사용자수(월요일 기준으로 체크)
        long weeklyActiveUserCount = userQueryRepository.countWeeklyActiveUsers();
        dashboardData.put("activeUserCount", weeklyActiveUserCount);

        // 3. 총 편지 수
        long totalLetterCount = letterRepository.count();
        dashboardData.put("totalLetters", totalLetterCount);

        //-------대시보드 표현 정보 --------
        // 1. 가입 전환율
        double conversionRate = userConversionService.getOverallConversionRate();
        dashboardData.put("conversionRate", conversionRate);

        // 2. 첫날 이탈율 데이터 (일자별)
        List<String> days = Arrays.asList("1W", "2W", "3W", "4W", "5W", "6W", "7W");
        dashboardData.put("days", days);
        // x축값 설정 날짜 값
        List<Integer> weeklyChurnData = churnAnalysisService.getDefaultWeeklyMenteeCounts();
        dashboardData.put("churnRate", weeklyChurnData);

        // 3. 고마움 표시 비율
        double letterAppreciationRate = letterAppreciationQueryRepository.calculateOverallAppreciationRate();
        dashboardData.put("gratitudeRate", letterAppreciationRate);

        // 4. 카테고리별 돌리기 버튼 클릭수
        // 카테고리 설정
        List<String> categories = Arrays.asList("커리어", "마음건강", "대인관계", "사랑", "가치관", "자산관리", "주거", "기타");
        dashboardData.put("categories", categories);
        // 카테고리별 데이터 입력
        List<Integer> categoryClicks = Arrays.asList(245, 187, 210, 176, 198);
        dashboardData.put("categoryClicks", categoryClicks);

        // 5. 전체 편지 답장율
        double overallReplyLetterRate = letterQueryStatusRepository.calculateOverallReplyRate();
        dashboardData.put("responseRate", overallReplyLetterRate);

        // 6. 활성 사용자 비율 (이번주에 로그인한 회원수)
        float weeklyActiveUser = 100 - Math.round((float) (totalUserCount - weeklyActiveUserCount) / totalUserCount * 100);
        dashboardData.put("activeUserRate", weeklyActiveUser);

        log.info("Total user count: {}", totalUserCount);
        log.info("active user count: {}", weeklyActiveUserCount);
        log.info("Total Letter Count: {}", totalLetterCount);
        log.info("conversionRate: {}", conversionRate);
        log.info("weeklyChurnData: {}", weeklyChurnData);
        log.info("gratitudeRate: {}", letterAppreciationRate);
        log.info("overallReplyLetterRate : {}", overallReplyLetterRate);
        log.info("weeklyActiveUser: {}", weeklyActiveUser);

        return ResponseEntity.ok(dashboardData);
    }
}
