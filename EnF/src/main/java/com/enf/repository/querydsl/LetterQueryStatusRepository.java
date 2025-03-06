package com.enf.repository.querydsl;

import com.enf.entity.QLetterEntity;
import com.enf.entity.QLetterStatusEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


// QueryDSL을 이요한 편지 통계 Repository
@Repository
@RequiredArgsConstructor
public class LetterQueryStatusRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QLetterStatusEntity letterStatus = QLetterStatusEntity.letterStatusEntity;

    /**
     * 전체 편지 답장률 계산
     *
     * @return 답장률 (0.0 ~ 1.0 사이의 값)
     */
    public double calculateOverallReplyRate() {
        // 전체 멘티가 보낸 편지 수 (letterStatus에서 menteeLetter가 not null인 것)
        Long totalMenteeLetters = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(letterStatus.menteeLetter.isNotNull())
                .fetchOne();

        if (totalMenteeLetters == null || totalMenteeLetters == 0) {
            return 0.0;
        }

        // 멘토가 답장한 편지 수 (letterStatus에서 mentorLetter가 not null인 것)
        Long mentorReplies = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(
                        letterStatus.menteeLetter.isNotNull(),
                        letterStatus.mentorLetter.isNotNull()
                )
                .fetchOne();

        if (mentorReplies == null) {
            return 0.0;
        }

        // 답장률 계산: 멘토 답장 수 / 멘티 편지 수
        return Math.round((double) mentorReplies / totalMenteeLetters *100);
    }

    /**
     * 특정 기간 내 편지 답장률 계산
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 답장률 (0.0 ~ 1.0 사이의 값)
     */
    public double calculateReplyRateForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        // 특정 기간 내 멘티가 보낸 편지 수
        Long totalMenteeLetters = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(
                        letterStatus.menteeLetter.isNotNull(),
                        createDateBetween(startDate, endDate)
                )
                .fetchOne();

        if (totalMenteeLetters == null || totalMenteeLetters == 0) {
            return 0.0;
        }

        // 특정 기간 내 멘티가 보낸 편지 중 멘토가 답장한 편지 수
        Long mentorReplies = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(
                        letterStatus.menteeLetter.isNotNull(),
                        letterStatus.mentorLetter.isNotNull(),
                        createDateBetween(startDate, endDate)
                )
                .fetchOne();

        if (mentorReplies == null) {
            return 0.0;
        }

        // 답장률 계산
        return (double) mentorReplies / totalMenteeLetters;
    }

    /**
     * 카테고리별 편지 답장률 계산
     *
     * @return 카테고리별 답장률 정보 목록
     */
    public List<CategoryReplyRate> calculateReplyRateByCategory() {
        QLetterEntity menteeLetter = new QLetterEntity("menteeLetter");

        return jpaQueryFactory
                .select(Projections.bean(CategoryReplyRate.class,
                        menteeLetter.categoryName.as("categoryName"),
                        Expressions.as(
                                JPAExpressions
                                        .select(letterStatus.count())
                                        .from(letterStatus)
                                        .where(letterStatus.menteeLetter.categoryName.eq(menteeLetter.categoryName)),
                                "totalCount"
                        ),
                        Expressions.numberTemplate(Double.class,
                                "CAST({0} AS DOUBLE) / NULLIF({1}, 0)",
                                JPAExpressions
                                        .select(letterStatus.count())
                                        .from(letterStatus)
                                        .where(
                                                letterStatus.menteeLetter.categoryName.eq(menteeLetter.categoryName),
                                                letterStatus.mentorLetter.isNotNull()
                                        ),
                                JPAExpressions
                                        .select(letterStatus.count())
                                        .from(letterStatus)
                                        .where(letterStatus.menteeLetter.categoryName.eq(menteeLetter.categoryName))
                        ).as("replyRate")
                ))
                .from(menteeLetter)
                .groupBy(menteeLetter.categoryName)
                .fetch();
    }

    /**
     * 특정 멘토의 답장률 계산
     *
     * @param mentorSeq 멘토 ID
     * @return 답장률 (0.0 ~ 1.0 사이의 값)
     */
    public double calculateMentorReplyRate(Long mentorSeq) {
        // 특정 멘토가 받은 편지 수
        Long receivedLetters = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(
                        letterStatus.mentor.userSeq.eq(mentorSeq),
                        letterStatus.menteeLetter.isNotNull()
                )
                .fetchOne();

        if (receivedLetters == null || receivedLetters == 0) {
            return 0.0;
        }

        // 특정 멘토가 답장한 편지 수
        Long repliedLetters = jpaQueryFactory
                .select(letterStatus.count())
                .from(letterStatus)
                .where(
                        letterStatus.mentor.userSeq.eq(mentorSeq),
                        letterStatus.menteeLetter.isNotNull(),
                        letterStatus.mentorLetter.isNotNull()
                )
                .fetchOne();

        if (repliedLetters == null) {
            return 0.0;
        }

        // 답장률 계산
        return (double) repliedLetters / receivedLetters;
    }

    /**
     * 평균 응답 시간 계산 (시간 단위)
     *
     * @return 평균 응답 시간 (시간)
     */
    public Double calculateAverageResponseTime() {
        // 각 편지 쌍에 대해 멘티 편지 생성시간과 멘토 편지 생성시간의 차이를 계산
        return jpaQueryFactory
                .select(Expressions.numberTemplate(Double.class,
                        "AVG(EXTRACT(EPOCH FROM {0}.create_at - {1}.create_at) / 3600.0)",
                        letterStatus.mentorLetter,
                        letterStatus.menteeLetter))
                .from(letterStatus)
                .where(
                        letterStatus.menteeLetter.isNotNull(),
                        letterStatus.mentorLetter.isNotNull()
                )
                .fetchOne();
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
     * 카테고리별 답장률 데이터 클래스
     */
    public static class CategoryReplyRate {
        private String categoryName;
        private Long totalCount;
        private Double replyRate;

        public CategoryReplyRate() {
        }

        public CategoryReplyRate(String categoryName, Long totalCount, Double replyRate) {
            this.categoryName = categoryName;
            this.totalCount = totalCount;
            this.replyRate = replyRate;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public Long getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Long totalCount) {
            this.totalCount = totalCount;
        }

        public Double getReplyRate() {
            return replyRate;
        }

        public void setReplyRate(Double replyRate) {
            this.replyRate = replyRate;
        }
    }

}
