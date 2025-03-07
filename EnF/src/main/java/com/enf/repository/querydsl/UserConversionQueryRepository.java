package com.enf.repository.querydsl;

import com.enf.entity.QUserEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;

/**
 * QueryDSL을 활용한 가입 전환율 통계 Repository
 */
@Repository
@RequiredArgsConstructor
public class UserConversionQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QUserEntity user = QUserEntity.userEntity;

    /**
     * 전체 가입 전환율 계산
     * Role이 3인 사용자는 전환되지 않은 사용자로 간주
     *
     * @return 가입 전환율 %
     */
    public double calculateOverallConversionRate() {
        // 전체 사용자 수
        Long totalUsers = jpaQueryFactory
                .select(user.count())
                .from(user)
                .fetchOne();

        if (totalUsers == null || totalUsers == 0) {
            return 0.0;
        }

        // Role이 3인 사용자 수 (전환되지 않은 사용자)
        Long nonConvertedUsers = jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(user.role.roleSeq.eq(3L))
                .fetchOne();

        if (nonConvertedUsers == null) {
            nonConvertedUsers = 0L;
        }

        // 전환된 사용자 수
        Long convertedUsers = totalUsers - nonConvertedUsers;

        // 전환율 계산: 전환된 사용자 수 / 전체 사용자 수
        return Math.round((double) convertedUsers / totalUsers * 100);
    }

    /**
     * 특정 기간 내 가입 전환율 계산
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 가입 전환율 (0.0 ~ 1.0 사이의 값)
     */
    public double calculateConversionRateForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        // 특정 기간 내 전체 사용자 수
        Long totalUsers = jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(createDateBetween(startDate, endDate))
                .fetchOne();

        if (totalUsers == null || totalUsers == 0) {
            return 0.0;
        }

        // 특정 기간 내 Role이 3인 사용자 수 (전환되지 않은 사용자)
        Long nonConvertedUsers = jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.role.roleSeq.eq(3L),
                        createDateBetween(startDate, endDate)
                )
                .fetchOne();

        if (nonConvertedUsers == null) {
            nonConvertedUsers = 0L;
        }

        // 전환된 사용자 수
        Long convertedUsers = totalUsers - nonConvertedUsers;

        // 전환율 계산
        return (double) convertedUsers / totalUsers;
    }

    /**
     * 제공자(provider)별 가입 전환율 계산
     *
     * @return 제공자별 전환율 정보 목록
     */
    public List<ProviderConversionRate> calculateConversionRateByProvider() {
        return jpaQueryFactory
                .select(Projections.bean(ProviderConversionRate.class,
                        user.provider.as("provider"),
                        user.count().as("totalCount"),
                        user.count().subtract(
                                jpaQueryFactory
                                        .select(user.count())
                                        .from(user)
                                        .where(
                                                user.provider.eq(user.provider),
                                                user.role.roleSeq.eq(3L)
                                        )
                        ).as("convertedCount")
                ))
                .from(user)
                .groupBy(user.provider)
                .fetch()
                .stream()
                .map(rate -> {
                    // 전환율 계산
                    if (rate.getTotalCount() > 0) {
                        double conversionRate = (double) rate.getConvertedCount() / rate.getTotalCount();
                        rate.setConversionRate(conversionRate);
                    } else {
                        rate.setConversionRate(0.0);
                    }
                    return rate;
                })
                .toList();
    }

    /**
     * 새 유형(bird)별 가입 전환율 계산
     *
     * @return 새 유형별 전환율 정보 목록
     */
    public List<BirdConversionRate> calculateConversionRateByBird() {
        return jpaQueryFactory
                .select(Projections.bean(BirdConversionRate.class,
                        user.bird.birdName.as("birdName"),
                        user.count().as("totalCount"),
                        user.count().subtract(
                                jpaQueryFactory
                                        .select(user.count())
                                        .from(user)
                                        .where(
                                                user.bird.birdSeq.eq(user.bird.birdSeq),
                                                user.role.roleSeq.eq(3L)
                                        )
                        ).as("convertedCount")
                ))
                .from(user)
                .groupBy(user.bird.birdName, user.bird.birdSeq)
                .fetch()
                .stream()
                .map(rate -> {
                    // 전환율 계산
                    if (rate.getTotalCount() > 0) {
                        double conversionRate = (double) rate.getConvertedCount() / rate.getTotalCount();
                        rate.setConversionRate(conversionRate);
                    } else {
                        rate.setConversionRate(0.0);
                    }
                    return rate;
                })
                .toList();
    }

    /**
     * 월별 가입 전환율 추이 계산
     *
     * @param months 조회할 최근 개월 수
     * @return 월별 전환율 정보 목록
     */
    public List<MonthlyConversionRate> calculateMonthlyConversionRates(int months) {
        LocalDateTime now = LocalDateTime.now();
        return jpaQueryFactory
                .select(Projections.bean(MonthlyConversionRate.class,
                        user.createAt.year().as("year"),
                        user.createAt.month().as("month"),
                        user.count().as("totalCount"),
                        user.count().subtract(
                                jpaQueryFactory
                                        .select(user.count())
                                        .from(user)
                                        .where(
                                                user.createAt.year().eq(user.createAt.year()),
                                                user.createAt.month().eq(user.createAt.month()),
                                                user.role.roleSeq.eq(3L)
                                        )
                        ).as("convertedCount")
                ))
                .from(user)
                .where(user.createAt.after(now.minusMonths(months)))
                .groupBy(user.createAt.year(), user.createAt.month())
                .orderBy(user.createAt.year().asc(), user.createAt.month().asc())
                .fetch()
                .stream()
                .map(rate -> {
                    // 전환율 계산
                    if (rate.getTotalCount() > 0) {
                        double conversionRate = (double) rate.getConvertedCount() / rate.getTotalCount();
                        rate.setConversionRate(conversionRate);
                    } else {
                        rate.setConversionRate(0.0);
                    }
                    return rate;
                })
                .toList();
    }

    // 날짜 범위 조건
    private BooleanExpression createDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }

        if (startDate == null) {
            return user.createAt.before(endDate);
        }

        if (endDate == null) {
            return user.createAt.after(startDate);
        }

        return user.createAt.between(startDate, endDate);
    }

    /**
     * 제공자별 전환율 데이터 클래스
     */
    @Setter
    @Getter
    public static class ProviderConversionRate {
        private String provider;
        private Long totalCount;
        private Long convertedCount;
        private Double conversionRate;

        public ProviderConversionRate() {
        }

    }

    /**
     * 새 유형별 전환율 데이터 클래스
     */
    @Setter
    @Getter
    public static class BirdConversionRate {
        private String birdName;
        private Long totalCount;
        private Long convertedCount;
        private Double conversionRate;

        public BirdConversionRate() {
        }

    }

    /**
     * 월별 전환율 데이터 클래스
     */
    @Setter
    @Getter
    public static class MonthlyConversionRate {
        private Integer year;
        private Integer month;
        private Long totalCount;
        private Long convertedCount;
        private Double conversionRate;

        public MonthlyConversionRate() {
        }

    }
}
