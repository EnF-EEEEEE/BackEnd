package com.enf.api.service;

import com.enf.domain.repository.querydsl.UserConversionQueryRepository.BirdConversionRate;
import com.enf.domain.repository.querydsl.UserConversionQueryRepository.MonthlyConversionRate;
import com.enf.domain.repository.querydsl.UserConversionQueryRepository.ProviderConversionRate;
import java.time.LocalDateTime;
import java.util.List;

public interface UserConversionService {

    /**
     * 전체 가입 전환율 조회
     *
     * @return 가입 전환율 (0.0 ~ 1.0 사이의 값)
     */
    double getOverallConversionRate();

    /**
     * 특정 기간 내 가입 전환율 조회
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 가입 전환율 (0.0 ~ 1.0 사이의 값)
     */
    double getConversionRateForPeriod(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 제공자별 가입 전환율 조회
     *
     * @return 제공자별 전환율 정보 목록
     */
    List<ProviderConversionRate> getConversionRateByProvider();

    /**
     * 새 유형별 가입 전환율 조회
     *
     * @return 새 유형별 전환율 정보 목록
     */
    List<BirdConversionRate> getConversionRateByBird();

    /**
     * 월별 가입 전환율 추이 조회
     *
     * @param months 조회할 최근 개월 수
     * @return 월별 전환율 정보 목록
     */
    List<MonthlyConversionRate> getMonthlyConversionRates(int months);
}
