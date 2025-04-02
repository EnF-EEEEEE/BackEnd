package com.enf.domain.repository.querydsl;

import com.enf.domain.entity.QLetterEntity;
import com.enf.domain.entity.QLetterStatusEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;

/**
 * QueryDSL을 활용한 편지 감사표시 통계 Repository
 */
@Repository
@RequiredArgsConstructor
public class LetterAppreciationQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QLetterStatusEntity letterStatus = QLetterStatusEntity.letterStatusEntity;

    /**
     * 전체 편지 감사표시 비율 계산
     *
     * @return 감사표시 비율 %
     */
    public double calculateOverallAppreciationRate() {
        // 멘티가 받은 전체 편지 수 (mentorLetter가 not null인 것)
        Long totalMentorLetters = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(letterStatus.mentorLetter.isNotNull())
                .fetchOne();

        if (totalMentorLetters == null || totalMentorLetters == 0) {
            return 0.0;
        }

        // 멘티가 읽은 편지 수 (isMenteeRead가 true인 것)
        Long readLetters = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(
                        letterStatus.mentorLetter.isNotNull(),
                        letterStatus.isMenteeRead.isTrue()
                )
                .fetchOne();

        if (readLetters == null) {
            readLetters = 0L;
        }

        // 멘티가 저장한 편지 수 (isMenteeSaved가 true인 것)
        Long savedLetters = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(
                        letterStatus.mentorLetter.isNotNull(),
                        letterStatus.isMenteeSaved.isTrue()
                )
                .fetchOne();

        if (savedLetters == null) {
            savedLetters = 0L;
        }

        // 저장 비율 계산: 저장한 편지 수 / 전체 편지 수
        return Math.round((double) savedLetters / totalMentorLetters * 100);
    }

    /**
     * 특정 기간 내 편지 감사표시 비율 계산
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 감사표시 비율 (0.0 ~ 1.0 사이의 값)
     */
    public double calculateAppreciationRateForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        // 특정 기간 내 멘티가 받은 편지 수
        Long totalMentorLetters = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(
                        letterStatus.mentorLetter.isNotNull(),
                        createDateBetween(startDate, endDate)
                )
                .fetchOne();

        if (totalMentorLetters == null || totalMentorLetters == 0) {
            return 0.0;
        }

        // 특정 기간 내 멘티가 저장한 편지 수
        Long savedLetters = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(
                        letterStatus.mentorLetter.isNotNull(),
                        letterStatus.isMenteeSaved.isTrue(),
                        createDateBetween(startDate, endDate)
                )
                .fetchOne();

        if (savedLetters == null) {
            savedLetters = 0L;
        }

        // 저장 비율 계산
        return (double) savedLetters / totalMentorLetters;
    }

    /**
     * 카테고리별 편지 감사표시 비율 계산
     *
     * @return 카테고리별 감사표시 비율 정보 목록
     */
    public List<CategoryAppreciationRate> calculateAppreciationRateByCategory() {
        QLetterEntity mentorLetter = new QLetterEntity("mentorLetter");

        // Tuple을 사용해 카테고리별 전체 편지 수를 조회
        List<Tuple> results = jpaQueryFactory
                .select(
                        mentorLetter.categoryName,
                        letterStatus.count()
                )
                .from(letterStatus)
                .join(mentorLetter).on(letterStatus.mentorLetter.letterSeq.eq(mentorLetter.letterSeq))
                .where(letterStatus.mentorLetter.isNotNull())
                .groupBy(mentorLetter.categoryName)
                .fetch();

        return results.stream()
                .map(tuple -> {
                    // 튜플에서 값을 안전하게 추출
                    String categoryName = tuple.get(mentorLetter.categoryName);
                    Long totalCount = tuple.get(letterStatus.count());

                    // 카테고리별 저장된 편지 수 조회
                    Long savedCount = jpaQueryFactory
                            .select(letterStatus.count())
                            .from(letterStatus)
                            .join(mentorLetter).on(letterStatus.mentorLetter.letterSeq.eq(mentorLetter.letterSeq))
                            .where(
                                    letterStatus.mentorLetter.isNotNull(),
                                    letterStatus.isMenteeSaved.isTrue(),
                                    mentorLetter.categoryName.eq(categoryName)
                            )
                            .fetchOne();

                    if (savedCount == null) {
                        savedCount = 0L;
                    }

                    double appreciationRate = totalCount > 0 ? (double) savedCount / totalCount : 0.0;

                    return new CategoryAppreciationRate(categoryName, totalCount, savedCount, appreciationRate);
                })
                .toList();
    }

    /**
     * 특정 멘토의 편지에 대한 감사표시 비율 계산
     *
     * @param mentorSeq 멘토 ID
     * @return 감사표시 비율 (0.0 ~ 1.0 사이의 값)
     */
    public double calculateMentorAppreciationRate(Long mentorSeq) {
        // 특정 멘토가 보낸 편지 수
        Long sentLetters = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(
                        letterStatus.mentor.userSeq.eq(mentorSeq),
                        letterStatus.mentorLetter.isNotNull()
                )
                .fetchOne();

        if (sentLetters == null || sentLetters == 0) {
            return 0.0;
        }

        // 특정 멘토가 보낸 편지 중 멘티가 저장한 편지 수
        Long savedLetters = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(
                        letterStatus.mentor.userSeq.eq(mentorSeq),
                        letterStatus.mentorLetter.isNotNull(),
                        letterStatus.isMenteeSaved.isTrue()
                )
                .fetchOne();

        if (savedLetters == null) {
            savedLetters = 0L;
        }

        // 감사표시 비율 계산
        return (double) savedLetters / sentLetters;
    }

    // 날짜 범위 조건
    private BooleanExpression createDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }

        if (startDate == null) {
            return letterStatus.createAt.before(endDate);
        }

        if (endDate == null) {
            return letterStatus.createAt.after(startDate);
        }

        return letterStatus.createAt.between(startDate, endDate);
    }

    /**
     * 카테고리별 감사표시 비율 데이터 클래스
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryAppreciationRate {
        private String categoryName;
        private Long totalCount;
        private Long savedCount;
        private Double appreciationRate;
    }
}